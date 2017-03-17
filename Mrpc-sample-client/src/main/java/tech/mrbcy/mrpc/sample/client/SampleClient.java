package tech.mrbcy.mrpc.sample.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import tech.mrbcy.mrpc.client.RpcProxy;
import tech.mrbcy.mrpc.sample.common.Person;
import tech.mrbcy.mrpc.sample.common.PersonService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class SampleClient {
	@Autowired
	private RpcProxy rpcProxy;
	
	
	@Test
	public void testPersonService(){
		
		PersonService personService = rpcProxy.createProxy(PersonService.class);
		Person person = new Person();
		person.setPersonId(10086);
		person.setPersonName("张三");
		
		System.out.println(personService.sayHello(person));
	}
}
