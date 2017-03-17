package tech.mrbcy.mrpc.demo.demo3.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tech.mrbcy.mrpc.demo.demo3.domain.Person;
import tech.mrbcy.mrpc.demo.demo3.domain.RpcRequest;
import tech.mrbcy.mrpc.demo.demo3.domain.RpcResponse;

public class ClientRunner {
	
	@Test
	public void testSinglePerson(){
		DemoRpcClient client = new DemoRpcClient("localhost", 9999);
		RpcRequest request = new RpcRequest();
		request.setId("00001");
		request.setMethodName("hello");
		request.setParamTypes(new Class<?>[]{Person.class});
		Person person = new Person();
		person.setPersonId(10086);
		person.setPersonName("张三");
		request.setArgs(new Object[]{person});
		
		try {
			RpcResponse response = client.send(request);
			System.out.println("服务器返回调用结果：" + response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPersons(){
		DemoRpcClient client = new DemoRpcClient("localhost", 9999);
		RpcRequest request = new RpcRequest();
		request.setId("00002");
		request.setMethodName("helloEveryone");
		request.setParamTypes(new Class<?>[]{List.class});
		List<Person> persons = new ArrayList<Person>();
		Person person1 = new Person();
		person1.setPersonId(10086);
		person1.setPersonName("张三");
		persons.add(person1);
		Person person2 = new Person();
		person2.setPersonId(10010);
		person2.setPersonName("李四");
		persons.add(person2);
		request.setArgs(new Object[]{persons});
		
		try {
			RpcResponse response = client.send(request);
			System.out.println("服务器返回调用结果：" + response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
