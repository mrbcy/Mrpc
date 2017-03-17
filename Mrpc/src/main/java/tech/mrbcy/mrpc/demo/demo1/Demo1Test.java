package tech.mrbcy.mrpc.demo.demo1;

import org.junit.Test;

public class Demo1Test {
	@Test
	public void testRpotostuffUtil(){
		User user = new User();
		user.setUserId(10000);
		user.setUserName("张三");
		user.setPw("123456");
		user.addAddress("北京市");
		user.addAddress("上海市");
		
		// 序列化
		byte[] bytes = ProtostuffUtil.serializer(user);
		System.out.println("数组长度：" + bytes.length);
		
		// 反序列化
		User newUser = ProtostuffUtil.deserializer(bytes, User.class);
		System.out.println(newUser);
	}
	
}
