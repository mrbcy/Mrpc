package tech.mrbcy.mrpc.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerRunner {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("applicationContext.xml");
	}
}
