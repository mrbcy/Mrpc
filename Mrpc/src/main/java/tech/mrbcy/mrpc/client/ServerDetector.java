package tech.mrbcy.mrpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;



public class ServerDetector implements Watcher {
	
	private String zkConnetionString;
	private String nodePath;
	private int sessionTimeout = 2000;
	private ZooKeeper zk;
	private ServerSwitchListener serverSwitchListener;
	private InetSocketAddress curServer;
	private CountDownLatch latch;

	/**
	 * 使用连接zk集群的字符串和节点路径创建探测器类
	 * @param zkConnetionString zk集群的连接字符串
	 * @param nodePath 节点路径
	 * @throws Exception 连接到zk集群过程中出错将抛出异常
	 */
	public ServerDetector(String zkConnetionString, String nodePath) throws Exception {
		this.zkConnetionString = zkConnetionString;
		
		if(nodePath.equals("") || nodePath.equals("/")){
			throw new RuntimeException("nodePath can't be empty");
		}
		if(!nodePath.startsWith("/")){
			nodePath = "/" + nodePath;
		}
		this.nodePath = nodePath;
		
		initZk();
		
	}

	// 初始化zk客户端
	private void initZk() throws Exception {
		latch = new CountDownLatch(1);
		this.zk = new ZooKeeper(zkConnetionString, sessionTimeout, this);
		latch.await(sessionTimeout, TimeUnit.MILLISECONDS);
		if(latch.getCount() > 0){
			throw new RuntimeException("Can not connect to ZooKeeper cluster " 
					+ zkConnetionString + ", please check and try again later");
		}
		// 注册监听
		getServerList();
		
	}

	// 刷新服务器列表
	private InetSocketAddress refreshServerList() throws Exception {
		// 获取服务器列表
		List<String> serversList = getServerList();
		boolean isCurServerAvailable = checkServerList(serversList);
		if(!isCurServerAvailable){
			this.curServer = chooseAServer(serversList);
			if(this.serverSwitchListener != null){
				serverSwitchListener.serverSwitched(this.curServer);
			}
		}
		
		return curServer;
	}

	private InetSocketAddress chooseAServer(List<String> servers) {
		if(servers == null || servers.size() == 0){
			return null;
		}
		
		// 随机选择一个服务器
		String serverAddr = servers.get(0);
		
		if(servers.size() > 1){
			int index = new Random().nextInt(servers.size());
			serverAddr = servers.get(index);
		}
		
		String[] addrAndPort = serverAddr.split(":");
		if(addrAndPort.length != 2){
			throw new RuntimeException("不合法的server地址：" + serverAddr);
		}
		
		return new InetSocketAddress(addrAndPort[0], Integer.parseInt(addrAndPort[1]));

	}

	private boolean checkServerList(List<String> servers) {
		if(servers == null || servers.size() == 0){
			return false;
		}
		// 未指定服务器地址或原服务器已失效，迁移到新的服务器
		if(servers.size() > 0 && curServer != null){
			for(String server:servers){
				if(server.equals(curServer.getHostString() + ":" + curServer.getPort())){
					return true;
				}
			}
		}
		
		return false;
	}

	private List<String> getServerList() throws Exception {
		List<String> serverNodes = zk.getChildren(nodePath, true);
		
		List<String> serverAddrs = new ArrayList<String>();
		
		for(String serverNode : serverNodes){
			serverAddrs.add(new String(zk.getData(nodePath+"/" + serverNode, null, null)));
		}
		return serverAddrs;
	}

	/**
	 * 获取一个可用的服务器连接地址
	 * @param serverSwitchListener 如果之前返回的服务器连接地址失效，将回调通知
	 * @return 没有可用的服务器时返回null
	 * @throws Exception
	 */
	public InetSocketAddress getAServer(
			ServerSwitchListener serverSwitchListener) throws Exception {
		this.serverSwitchListener = null;
		InetSocketAddress newAddress = refreshServerList();
		this.serverSwitchListener = serverSwitchListener;
		return newAddress;
	}

	public void process(WatchedEvent event) {
		System.out.println("detector:------" + event);
		if(event.getState() == Event.KeeperState.SyncConnected){
			latch.countDown();
		}
		if(event.getState() == Event.KeeperState.Expired){
			try {
				// 会话过期，重新连接ZooKeeper集群
				initZk();			
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if(event.getType() == EventType.NodeChildrenChanged){
			try {
				refreshServerList();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}


}
