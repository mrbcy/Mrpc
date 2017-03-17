package tech.mrbcy.mrpc.demo.demo4;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {
	
	@SuppressWarnings("unchecked")
	public<T> T createProxy(Class<T> interfaceClass){
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), 
				new Class<?>[]{interfaceClass}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				System.out.println("方法已调用：" + method);
				// 下面可以连接服务器，发送调用请求，然后返回服务器的结果了
				return null;
			}
		});
	}
	
}
