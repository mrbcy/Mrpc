package tech.mrbcy.mrpc.demo.demo2;

import org.junit.Test;

public class MockServer {
	@Test
	public void registToZK(){
		ServerAddrHelper helper = new ServerAddrHelper("amaster:2181,anode1:2181,anode2:2181");
		try {
			helper.registServer("ServiceImplServer", "localhost:9000");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("注册服务器失败");
		}
	}
}
