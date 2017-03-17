package tech.mrbcy.mrpc.server;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;





public class ServerRegister implements Watcher{
	private int sessionTimeout = 2000;
	
	private ZooKeeper zk;

	private String groupName;
	private CountDownLatch latch;
	private Map<String,String> createdTempNodes = new HashMap<String,String>();
	private String zkConnetionString;
	
	/**
	 * 创建一个新的服务器地址注册器
	 * @param zkConnetionString ZooKeeper集群的连接地址
	 * @param groupName 存放服务器地址的父路径
	 * @throws IOException 
	 */
	public ServerRegister(String zkConnetionString, String groupName) throws Exception {
		this.zkConnetionString = zkConnetionString;
		this.groupName = groupName;
		
		if(!groupName.startsWith("/")){
			groupName = "/" + groupName;
		}
		
		String[] parents = groupName.split("/");
		for(int i = 1; i < parents.length; i++){
			if(parents[i].equals("") || parents[i].trim().length() == 0){
				throw new RuntimeException(groupName + " can't be used as a groupName");
			}
		}
		
		initZk();
		
	}

	private void initZk() throws Exception {
		latch = new CountDownLatch(1);
		this.zk = new ZooKeeper(zkConnetionString, sessionTimeout, this);
		latch.await(sessionTimeout + 100, TimeUnit.MILLISECONDS);
		if(latch.getCount() > 0){
			throw new RuntimeException("Can not connect to ZooKeeper cluster " 
					+ zkConnetionString + ", please check and try again later");
		}
		
	}

	/**
	 * 注册服务器地址到ZooKeeper集群
	 * @param nodePath 注册节点的路径地址，真实注册时会在之前拼接groupName
	 * @param serverAddr 长度在1-255之间，不符合将抛出RuntimeException
	 * @throws Exception 
	 */
	public void registServer(String nodePath, String serverAddr) throws Exception {
		if(serverAddr.length() == 0 || serverAddr.length() > 255){
			throw new RuntimeException("the serverAddr's length should between 1 and 255");
		}
		if(nodePath.equals("") || nodePath.equals("/")){
			throw new RuntimeException("the nodePath can't be empty string");
		}
		
		if(!nodePath.startsWith("/")){
			nodePath = "/" + nodePath;
		}
		// 检查父节点是否存在，不存在就循环创建
		createParentPath();
		
		String node = zk.create(groupName + nodePath, serverAddr.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		createdTempNodes.put(node,serverAddr);
	}

	// 创建必要的父节点
	private void createParentPath() throws Exception{
		Stat groupStat = zk.exists(this.groupName, null);
		if(groupStat == null){
			String[] parents = groupName.split("/");
			String curPath = "";
			
			for(int i = 1; i < parents.length; i++){
				curPath = curPath + "/" + parents[i];
				Stat stat = zk.exists(curPath, null);
				if(stat == null){
					zk.create(curPath, new String("Mrpc framework server list node").getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
			}

			
		}
		
	}

	/**
	 * 注销之前通过此对象注册的所有服务器路径
	 * @throws Exception
	 */
	public void unregist() throws Exception {
		zk.close();
		createdTempNodes = new HashMap<String, String>();
	}

	// 处理zk事件
	public void process(WatchedEvent event) {
		System.out.println("register:------" + event);
		if(event.getState() == Event.KeeperState.SyncConnected){
			latch.countDown();
			try {
				// 逐个查找之前创建过的临时节点，不存在的重新创建
				Map<String,String> newTempNodes = new HashMap<String,String>();
				for(Map.Entry<String, String> entry:createdTempNodes.entrySet()){
					Stat stat = zk.exists(entry.getKey(), null);
					if(stat == null){
						// 重新创建
						String newNode = zk.create(entry.getKey(), entry.getValue().getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
						newTempNodes.put(newNode, entry.getValue());
					}else{
						// 还存在，不需要重新创建
						newTempNodes.put(entry.getKey(), entry.getValue());
					}
				}
				createdTempNodes = newTempNodes;
			} catch (Exception e) {
				
			}
		}
		if(event.getState() == Event.KeeperState.Expired){
			try {
				// 会话过期，重新连接ZooKeeper集群
				initZk();			
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
}
