package tech.mrbcy.mrpc.demo.demo2;

import java.net.InetSocketAddress;
import java.util.List;

import org.junit.Test;

public class MockClient {
	
	private InetSocketAddress serverAddress;
	
	@Test
	public void testClient(){
		
		
		ServerAddrHelper serverHelper = new ServerAddrHelper("amaster:2181,anode1:2181,anode2:2181");
		ServerAddrHelper helper = new ServerAddrHelper("amaster:2181,anode1:2181,anode2:2181");
		try {
			serverHelper.registServer("ServiceImplServer", "localhost:10000");
			List<String> serverList = helper.discoverServers(new ServerChangeListener() {
				
				public void onChange(List<String> servers) {
					System.out.println("服务器列表发生变化，当前服务器列表为：");
					System.out.println(servers);
					
					changeToServer(servers);
				}

			});
			System.out.println(serverList);
			if(serverList == null || serverList.size() == 0){
				System.out.println("没有可用的服务器");
			}
			changeToServer(serverList);
			Thread.sleep(1000);
			serverHelper.registServer("ServiceImplServer", "localhost:10001");
			Thread.sleep(1000);
			serverHelper.registServer("ServiceImplServer", "localhost:10002");
			Thread.sleep(500000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void changeToServer(List<String> servers) {
		if(servers == null || servers.size() == 0){
			return;
		}
		// 未指定服务器地址或原服务器已失效，迁移到新的服务器
		boolean valid = false;
		if(servers.size() > 0 && serverAddress != null){
			for(String server:servers){
				if(server.equals(serverAddress.getHostString() + ":" + serverAddress.getPort())){
					valid = true;
					break;
				}
			}
		}
		if(serverAddress == null || !valid){
			serverAddress = ServerLoadBalancer.chooseServer(servers);
			System.out.println("未指定服务器地址或原服务器已失效，迁移到新的服务器:" + serverAddress.getHostString() + ":" + serverAddress.getPort());
		}
		
	}
}
