package tech.mrbcy.mrpc.demo.demo3.server;


import java.lang.reflect.Method;



import tech.mrbcy.mrpc.demo.demo3.SampleServiceImpl;
import tech.mrbcy.mrpc.demo.demo3.domain.RpcRequest;
import tech.mrbcy.mrpc.demo.demo3.domain.RpcResponse;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DemoRpcServerHandler extends ChannelInboundHandlerAdapter  {
	private SampleServiceImpl serviceImpl = new SampleServiceImpl();
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		RpcResponse response = new RpcResponse();
		try {
			RpcRequest request = (RpcRequest) msg;
			System.out.println("服务器收到调用请求：" + request);
			response.setId(request.getId());
			Method method = SampleServiceImpl.class.getDeclaredMethod(request.getMethodName(), request.getParamTypes());
			Object result = method.invoke(serviceImpl, request.getArgs());
			response.setResult(result);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setError(e);
		}
		
		ChannelFuture f = ctx.writeAndFlush(response);
	    f.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
	}


}
