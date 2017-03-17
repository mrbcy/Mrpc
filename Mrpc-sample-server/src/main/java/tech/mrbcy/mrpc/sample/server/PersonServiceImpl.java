package tech.mrbcy.mrpc.sample.server;

import tech.mrbcy.mrpc.sample.common.Person;
import tech.mrbcy.mrpc.sample.common.PersonService;
import tech.mrbcy.mrpc.server.RpcService;

@RpcService("tech.mrbcy.mrpc.sample.common.PersonService")
public class PersonServiceImpl implements PersonService {

	public String sayHello(Person person) {
		return "hello, " + person;
	}

}
