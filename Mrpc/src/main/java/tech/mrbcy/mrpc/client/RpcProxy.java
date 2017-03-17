package tech.mrbcy.mrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;

import tech.mrbcy.mrpc.common.RpcRequest;
import tech.mrbcy.mrpc.common.RpcResponse;

public class RpcProxy {
	
	private InetSocketAddress serverAddr;
	private ServerDetector serverDetector;
	
	public RpcProxy(String serverAddr){
		String arr[] = serverAddr.split(":");
		int port = Integer.parseInt(arr[1]);
		this.serverAddr = new InetSocketAddress(arr[0], port);
	}
	public RpcProxy(ServerDetector serverDetector) throws Exception{
		this.serverDetector = serverDetector;
		this.serverAddr = serverDetector.getAServer(new ServerSwitchListener() {
			
			public void serverSwitched(InetSocketAddress newServerAddr) {
				RpcProxy.this.serverAddr = newServerAddr;
			}
		});
		
	}
	
	@SuppressWarnings("unchecked")
	public<T> T createProxy(final Class<T> interfaceClass){
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), 
				new Class<?>[]{interfaceClass}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				System.out.println("方法已调用：" + method);
				
				if(RpcProxy.this.serverAddr == null){
					throw new RuntimeException("There is no available server to do the rpc");
				}
				//创建RpcRequest，封装被代理类的属性
				RpcRequest request = new RpcRequest();
				request.setId(UUID.randomUUID().toString());
				//拿到声明这个方法的业务接口名称
				request.setInterfaceName(interfaceClass.getName());
				request.setMethodName(method.getName());
				request.setParamTypes(method.getParameterTypes());
				request.setArgs(args);
				
				RpcClient client = new RpcClient(serverAddr.getHostString(), serverAddr.getPort());

				//通过netty向服务端发送请求
				RpcResponse response = client.send(request);
				//返回信息
				if (response.isSuccess()) {
					return response.getResult();
				} else {
					throw response.getError();
				}
			}
		});
	}
	
}
