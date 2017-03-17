package tech.mrbcy.mrpc.demo.demo3;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import tech.mrbcy.mrpc.demo.demo3.domain.Person;

public class SampleServiceImpl {
	public String hello(String text){
		return "hello " + text;
	}
	
	public String hello(Person person){
		return "hello " + person.getPersonName() + ", your id is " + person.getPersonId();
	}
	
	public List<String> helloEveryone(List<Person> persons){
		List<String> helloList = new ArrayList<String>();
		
		for(Person person : persons){
			helloList.add(hello(person));
		}


		return helloList;
	}
}
