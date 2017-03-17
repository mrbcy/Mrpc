package tech.mrbcy.mrpc.server;


import java.lang.reflect.Method;
import java.util.Map;

import tech.mrbcy.mrpc.common.RpcRequest;
import tech.mrbcy.mrpc.common.RpcResponse;




import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcServerHandler extends ChannelInboundHandlerAdapter  {
	private Map<String, Object> handlerMap;
	
	public RpcServerHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		RpcResponse response = new RpcResponse();
		try {
			RpcRequest request = (RpcRequest) msg;
			System.out.println("服务器收到调用请求：" + request);
			response.setId(request.getId());
			
			response.setResult(handle(request));
		} catch (Exception e) {
			response.setSuccess(false);
			response.setError(e);
		}
		
		ChannelFuture f = ctx.writeAndFlush(response);
	    f.addListener(ChannelFutureListener.CLOSE);
	}

	private Object handle(RpcRequest request) throws Exception {
		String interfaceName = request.getInterfaceName();
		
		// 获得servicebean
		Object serviceBean = handlerMap.get(interfaceName);
		
		//拿到要调用的方法名、参数类型、参数值
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParamTypes();
		Object[] parameters = request.getArgs();
		
		//拿到接口类
		Class<?> forName = Class.forName(interfaceName);
		
		//调用实现类对象的指定方法并返回结果
		Method method = forName.getMethod(methodName, parameterTypes);
		return method.invoke(serviceBean, parameters);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
	}


}
