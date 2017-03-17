package tech.mrbcy.mrpc.demo.demo4;

import org.junit.Test;

import sun.print.SunMinMaxPage;

public class InterfaceTest {
	@Test
	public void testRpcProxy(){
		HelloService service = new RpcProxy().createProxy(HelloService.class);
		service.hello("张三");
	}
	
	@Test
	public void testMoney(){
		double sum = 0;
		
		for(int i = 1; i <= 365; i++){
			sum += i*0.1;
		}
		System.out.println(sum);
	}
}
