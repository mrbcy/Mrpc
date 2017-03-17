package tech.mrbcy.mrpc.test.utiltest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import tech.mrbcy.mrpc.test.domain.User;
import tech.mrbcy.mrpc.test.domain.UserListPack;
import tech.mrbcy.mrpc.test.enumm.UserType;
import tech.mrbcy.mrpc.util.ProtostuffUtil;

/*
 * 对ProtoStuffUtil的测试主要分为2个
 * 第一个是能够对复杂的对象进行正确的编码解码
 * 第二个是对复杂对象构成的List、Map进行正确的编码解码
 */
public class ProtoStuffUtilTest {

	@Test
	// 对复杂对象进行解码编码
	public void testObject(){
		User user = createUser(10086, "张三");
		
		doCompare(user);
	}
	
	@Test(expected = ConcurrentModificationException.class)
	// 对复杂对象的列表进行编码解码
	public void testList(){
		List<User> users = new ArrayList<User>();
		
		users.add(createUser(10086, "张三"));
		users.add(createUser(10010, "李四"));
		
		doCompare(users);	
	}
	
	@Test
	// 把List包到对象里进行编码解码
	public void testUserPack(){
		List<User> users = new ArrayList<User>();
		
		users.add(createUser(10086, "张三"));
		users.add(createUser(10010, "李四"));
		
		UserListPack ulp = new UserListPack();
		ulp.setUsers(users);
		
		doCompare(ulp);
	}
	
	@Test
	public void testMap(){
		Map<Integer, User> uMap = new HashMap<Integer, User>();
		uMap.put(1,createUser(10086, "张三"));
		uMap.put(2,createUser(10010, "李四"));
		
		doCompare(uMap,"{}");
	}
	
	private User createUser(Integer userId, String userName){
		User user = new User();
		user.setUserId(userId);
		user.setUserName(userName);
		user.setLockState(true);
		user.setUserType(UserType.VIP_USER);
		user.addAddress("上海");
		user.addAddress("北京");
		user.putFavor("tdd", "当当网");
		user.putFavor("java","Amazon");
		return user;
	}
	
	private void doCompare(Object oldObj){
		// 保存转换之前的toString结果
		String oldString = oldObj.toString();
		doCompare(oldObj, oldString);
	}
	
	private void doCompare(Object oldObj,String expectStr){
		// 转换
		byte[] data = ProtostuffUtil.serializer(oldObj);
		
		Object newObj = ProtostuffUtil.deserializer(data, oldObj.getClass());
		
		// 保存转换之后的toString结果
		String newString = newObj.toString();
		
		assertEquals(expectStr,newString);
	}
}
