package tech.mrbcy.mrpc.demo.demo2;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class ServerLoadBalancer {
	/**
	 * 选择一个服务器
	 * @param servers 服务器列表 示例值：133.122.5.88:8888 或 anode2:5884
	 * @return  连接服务器地址
	 */
	public static InetSocketAddress chooseServer(List<String> servers){
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
}
