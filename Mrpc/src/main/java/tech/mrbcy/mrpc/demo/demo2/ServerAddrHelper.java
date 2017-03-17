package tech.mrbcy.mrpc.demo.demo2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ServerAddrHelper {
	public static final String DEFAULT_GROUP_NAME = "/MrpcServers";
	private String connString;
	private String groupName;
	private ZooKeeper zk;
	private ServerChangeListener listener;
	private static int sessionTimeout = 2000;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	public ServerAddrHelper(String connString){
		this.connString = connString;
		this.groupName = DEFAULT_GROUP_NAME;
	}
	
	/**
	 * 
	 * @param connString zk连接字符串
	 * @param groupName 父节点路径，位于/下，需要带/ 示例值："/MrpcServers"
	 */
	public ServerAddrHelper(String connString, String groupName){
		this.connString = connString;
		if(!groupName.startsWith("/")){
			groupName = "/" + groupName;
		}
		this.groupName = groupName;
	}
	
	/**
	 * 向ZooKeeper集群注册服务器
	 * @param registPath 服务器节点路径，示例值"server"
	 * @param address 服务器地址及端口号，用于客户端连接
	 * @throws Exception 连接服务器失败

	 */
	public void registServer(String registPath, String address) throws Exception{
		zk = new ZooKeeper(connString,sessionTimeout,null);
		
		// 判断父目录是否存在，不存在则创建
		Stat groupStat = zk.exists(groupName, false);
		if(groupStat == null){
			zk.create(groupName, "Mrpc server list".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		
		// 注册服务器
		if(!registPath.startsWith("/")){
			registPath = "/" + registPath;
		}
		String registAddr = zk.create(groupName+registPath, address.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("Server is starting, reg addr：" + registAddr);
	}
	
	/**
	 * 发现服务器
	 * @param listener 监听器，如果不是null，等到服务器列表发生变化时，监听器会收到通知
	 * @return
	 * @throws Exception
	 */
	public List<String> discoverServers(ServerChangeListener listener) throws Exception{
		this.listener = listener;
		zk = new ZooKeeper(connString,sessionTimeout,new Watcher(){

			public void process(WatchedEvent event) {
				System.out.println(event);
				if(event.getState() == Event.KeeperState.SyncConnected){
					latch.countDown();
				}
				if(event.getType() == EventType.NodeChildrenChanged){
					// 服务器列表发生变化
					try {
						List<String> servers = getServerList();
						if(ServerAddrHelper.this.listener != null){
							ServerAddrHelper.this.listener.onChange(servers);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		latch.await();
		
		return getServerList();
	}

	private List<String> getServerList() throws Exception {
		zk.getChildren(groupName, true);
		List<String> children = zk.getChildren(groupName, true);
		List<String> servers = new ArrayList<String>();
		for(String child : children) {
			byte[] data = zk.getData(groupName+"/"+child, null, null);
			servers.add(new String(data));
		}
		return servers;
	}
	
	
	
	
}
