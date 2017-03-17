package tech.mrbcy.mrpc.test.client;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tech.mrbcy.mrpc.client.ServerDetector;
import tech.mrbcy.mrpc.client.ServerSwitchListener;
import tech.mrbcy.mrpc.server.ServerRegister;

public class ServerDetectorTest {
	private String zkConnetionString = "amaster:2181,anode1:2181,anode2:2181";
	private String groupName = "/MrpcServer";
	private String serverNode = "/ServiceImplServer";
	
	private int closeCount = 0;
	private CountDownLatch latch;
	private boolean reconnect = false;
	
	@Before
	public void init(){
		closeCount = 0;
		latch = new CountDownLatch(1);
		reconnect = false;
	}
	
	@Test
	public void testServerChange() throws Exception{
		final ServerRegister sr1 = new ServerRegister(zkConnetionString, groupName);
		sr1.registServer(serverNode,"localhost:8000");
		
		final ServerRegister sr2 = new ServerRegister(zkConnetionString, groupName);
		sr2.registServer(serverNode,"localhost:8001");
		
		final ServerRegister sr3 = new ServerRegister(zkConnetionString, groupName);
		sr3.registServer(serverNode,"localhost:8002");
		
		ServerDetector serverDetector = new ServerDetector(zkConnetionString,groupName);
		
		
		InetSocketAddress serverAddr = serverDetector.getAServer(new ServerSwitchListener() {
			
			public void serverSwitched(InetSocketAddress newServerAddr) {
				if(newServerAddr == null){
					latch.countDown();
				}else{
					try {
						closeServer(sr1,sr2,sr3,newServerAddr);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
		Assert.assertNotNull(serverAddr);
		closeServer(sr1,sr2,sr3,serverAddr);
		latch.await();
		Assert.assertEquals(closeCount, 3);
	}
	
	@Test(expected = RuntimeException.class)
	public void testUnAvailableZk() throws Exception{
		new ServerDetector("anode3:2181",groupName);
	}
	
	@Test
	public void testNoListener() throws Exception{
		final ServerRegister sr1 = new ServerRegister(zkConnetionString, groupName);
		sr1.registServer(serverNode,"localhost:8000");
		
		final ServerRegister sr2 = new ServerRegister(zkConnetionString, groupName);
		sr2.registServer(serverNode,"localhost:8001");
		
		final ServerRegister sr3 = new ServerRegister(zkConnetionString, groupName);
		sr3.registServer(serverNode,"localhost:8002");
		
		ServerDetector serverDetector = new ServerDetector(zkConnetionString,groupName);
		
		InetSocketAddress serverAddr = serverDetector.getAServer(null);
		System.out.println("获得服务器地址：" + serverAddr.getHostString() + ":" + serverAddr.getPort());
		
	}
	
	@Test
	public void testNoServer() throws Exception{
		ServerDetector serverDetector = new ServerDetector(zkConnetionString,groupName);
		
		InetSocketAddress serverAddr = serverDetector.getAServer(null);
		Assert.assertNull(serverAddr);
	}
	
	@Test(expected = RuntimeException.class)
	public void testEmptyNodeString() throws Exception{
		new ServerDetector(zkConnetionString,"");
	}
	
	@Test(expected = RuntimeException.class)
	public void testObliqueNodeString() throws Exception{
		new ServerDetector(zkConnetionString,"");
	}
	
	@Test
	public void testNodePathNotStartWithOblique() throws Exception{
		final ServerRegister sr1 = new ServerRegister(zkConnetionString, groupName);
		sr1.registServer(serverNode,"localhost:8000");
		
		final ServerRegister sr2 = new ServerRegister(zkConnetionString, groupName);
		sr2.registServer(serverNode,"localhost:8001");
		
		final ServerRegister sr3 = new ServerRegister(zkConnetionString, groupName);
		sr3.registServer(serverNode,"localhost:8002");
		
		ServerDetector serverDetector = new ServerDetector(zkConnetionString,"MrpcServer");
		
		InetSocketAddress serverAddr = serverDetector.getAServer(null);
		System.out.println("获得服务器地址：" + serverAddr.getHostString() + ":" + serverAddr.getPort());
		
	}
	
	@Test
	public void testReconnect() throws Exception {
		
		final ServerRegister sr1 = new ServerRegister(zkConnetionString, groupName);
		sr1.registServer(serverNode,"localhost:8000");
		
		final ServerRegister sr2 = new ServerRegister(zkConnetionString, groupName);
		sr2.registServer(serverNode,"localhost:8001");
		
		final ServerRegister sr3 = new ServerRegister(zkConnetionString, groupName);
		sr3.registServer(serverNode,"localhost:8002");
		
		ServerDetector serverDetector = new ServerDetector(zkConnetionString,"MrpcServer");
		
		InetSocketAddress serverAddr = serverDetector.getAServer(new ServerSwitchListener() {
			
			public void serverSwitched(InetSocketAddress newServerAddr) {
				System.out.println("获得服务器地址：" + newServerAddr.getHostString() + ":" + newServerAddr.getPort());
				reconnect = true;
				latch.countDown();
			}
		});
		System.out.println("获得服务器地址：" + serverAddr.getHostString() + ":" + serverAddr.getPort());
		
		// 手动重启zk集群
		
		latch.await(200, TimeUnit.SECONDS);
		closeServer(sr1, sr2, sr3, serverAddr);
		
		Thread.sleep(10000);
		Assert.assertTrue(reconnect);
	}

	private void closeServer(ServerRegister sr1, ServerRegister sr2,
			ServerRegister sr3, InetSocketAddress serverAddr) throws Exception {
		closeCount += 1;
		
		if(serverAddr.getPort() == 8000){
			sr1.unregist();
		}else if(serverAddr.getPort() == 8001){
			sr2.unregist();
		}else if(serverAddr.getPort() == 8002){
			sr3.unregist();
		}
		
	}

	
	
	
}
