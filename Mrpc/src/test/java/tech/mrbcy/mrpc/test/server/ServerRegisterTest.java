package tech.mrbcy.mrpc.test.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tech.mrbcy.mrpc.server.ServerRegister;

public class ServerRegisterTest {
	private String zkConnetionString = "amaster:2181,anode1:2181,anode2:2181";
	private int sessionTimeout = 2000;
	private int waitTimeout = 1100;
	
	private String groupName = "/MrpcServer";
	private String serverNode = "/ServiceImplServer";
	
	@Before
	public void init(){
		try {
			ZooKeeper zkClient = new ZooKeeper(zkConnetionString, sessionTimeout, null);
			Thread.sleep(waitTimeout);
			rmr(zkClient,groupName);
		} catch (Exception e) {
		}
		
	}
	
	
	
	@Test
	public void testRegistAndUnRegist() throws Exception{
		try {
			
			ServerRegister serverRegister = new ServerRegister(zkConnetionString,groupName);
			serverRegister.registServer(serverNode,"localhost:8000");
			
			ServerRegister serverRegister2 = new ServerRegister(zkConnetionString,groupName);
			serverRegister2.registServer(serverNode,"localhost:8001");
			
			ServerRegister serverRegister3 = new ServerRegister(zkConnetionString,groupName);
			serverRegister3.registServer(serverNode,"localhost:8002");
			
			// 获取服务器地址，检查是否包含所有的3个服务器地址
			List<String> serverList = getServerList();
			
			checkServerList(serverList,new String[]{"localhost:8000","localhost:8001","localhost:8002"});
			
			serverRegister2.unregist();
			serverList = getServerList();
			checkServerList(serverList,new String[]{"localhost:8000","localhost:8002"});
			
			serverRegister.unregist();
			serverRegister3.unregist();
			serverList = getServerList();
			checkServerList(serverList,new String[]{});
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void testZkReconnect() throws Exception{
		try {
			
			ServerRegister serverRegister = new ServerRegister(zkConnetionString,groupName);
			serverRegister.registServer(serverNode,"localhost:8000");
			
			ServerRegister serverRegister2 = new ServerRegister(zkConnetionString,groupName);
			serverRegister2.registServer(serverNode,"localhost:8001");
			
			ServerRegister serverRegister3 = new ServerRegister(zkConnetionString,groupName);
			serverRegister3.registServer(serverNode,"localhost:8002");
			
			//Thread.sleep(100000);
			
			// 关闭ZooKeeper集群并在100秒之内重新启动ZooKeeper集群
			
			Thread.sleep(sessionTimeout);
			// 获取服务器地址，检查是否包含所有的3个服务器地址
			List<String> serverList = getServerList();
			
			checkServerList(serverList,new String[]{"localhost:8000","localhost:8001","localhost:8002"});
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	@Test(expected=RuntimeException.class)
	public void testUnavailableZK() throws Exception{
		new ServerRegister("anode3:2181", groupName);
	}
	
	@Test(expected=RuntimeException.class)
	public void testEmptyServerAddr() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,groupName);
		serverRegister.registServer(serverNode, "");
	}
	
	@Test(expected=RuntimeException.class)
	public void testOverLengthServerAddr() throws Exception{
		String serverAddr = "";
		for(int i = 0; i < 256; i++){
			serverAddr = "a" + serverAddr;
		}
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,groupName);
		serverRegister.registServer(serverNode, serverAddr);
	}
	
	@Test(expected=RuntimeException.class)
	public void testEmptyServerNode() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,groupName);
		serverRegister.registServer("", "localhost:8000");
	}
	
	@Test(expected=RuntimeException.class)
	public void testEmptyServerNodeWithOblique() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,groupName);
		serverRegister.registServer("/", "localhost:8000");
	}
	
	@Test
	public void testEmptyServerNodeWithoutOblique() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,groupName);
		serverRegister.registServer("ServiceImplServer", "localhost:8000");
		// 获取服务器地址，检查是否包含指定的服务器地址
		List<String> serverList = getServerList();
		
		checkServerList(serverList,new String[]{"localhost:8000"});
	}
	
	@Test(expected=RuntimeException.class)
	public void testEmptyGroupName() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,"");
		serverRegister.registServer(serverNode, "localhost:8000");
	}
	
	@Test(expected=RuntimeException.class)
	public void testEmptyGroupNameWithOblique() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,"/");
		serverRegister.registServer(serverNode, "localhost:8000");
	}
	
	@Test(expected=RuntimeException.class)
	public void testEmptyGroupNameWithoutOblique() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,"MrpcServer");
		serverRegister.registServer(serverNode, "localhost:8000");
		// 获取服务器地址，检查是否包含指定的服务器地址
		List<String> serverList = getServerList();
		
		checkServerList(serverList,new String[]{"localhost:8000"});
	}
	
	@Test
	public void testParentPathNotExist() throws Exception{
		ServerRegister serverRegister = new ServerRegister(zkConnetionString,"/test/ddd/MrpcServer");
		serverRegister.registServer(serverNode, "localhost:8000");
		// 获取服务器地址，检查是否包含指定的服务器地址
		List<String> serverList = getServerList("/test/ddd/MrpcServer");
		
		checkServerList(serverList,new String[]{"localhost:8000"});
	}
	
	/**
     * 递归删除 因为zookeeper只允许删除叶子节点，如果要删除非叶子节点，只能使用递归
     * @param path
     * @throws IOException
     */
    private void rmr(ZooKeeper zk,String path) throws Exception {
        //获取路径下的节点
        List<String> children = zk.getChildren(path, false);
        for (String pathCd : children) {
            //获取父节点下面的子节点路径
            String newPath = "";
            //递归调用,判断是否是根节点
            if (path.equals("/")) {
                newPath = "/" + pathCd;
            } else {
                newPath = path + "/" + pathCd;
            }
            rmr(zk,newPath);
        }
        //删除节点,并过滤zookeeper节点和 /节点
        if (path != null && !path.trim().startsWith("/zookeeper") && !path.trim().equals("/")) {
            zk.delete(path, -1);
        }
    }

	private void checkServerList(List<String> serverList, String[] expectList) {
		Assert.assertEquals(serverList.size(), expectList.length);
		
		if(expectList.length == 0){
			return;
		}
		
		for(String server : expectList){
			boolean findEqual = false;
			for(int i = 0; i < serverList.size(); i++){
				if(server.equals(serverList.get(i))){
					findEqual = true;
					break;
				}
			}
			Assert.assertEquals(findEqual, true);
		}
		
	}

	private List<String> getServerList() throws Exception {
		return getServerList(groupName);
	}
	
	private List<String> getServerList(String groupName) throws Exception {
		ZooKeeper zkClient = new ZooKeeper(zkConnetionString, sessionTimeout, null);
		Thread.sleep(waitTimeout);
		List<String> serverNodes = zkClient.getChildren(groupName, false);
		
		List<String> serverAddrs = new ArrayList<String>();
		
		for(String serverNode : serverNodes){
			serverAddrs.add(new String(zkClient.getData(groupName+"/" + serverNode, null, null)));
		}
		return serverAddrs;
	}
}
