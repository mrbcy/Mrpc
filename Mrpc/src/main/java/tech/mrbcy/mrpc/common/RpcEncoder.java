package tech.mrbcy.mrpc.common;

import tech.mrbcy.mrpc.demo.demo1.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class RpcEncoder extends ChannelOutboundHandlerAdapter {
	private Class clazz;
	
	public RpcEncoder(Class clazz){
		this.clazz = clazz;
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if(clazz.isInstance(msg)){
			byte[] data = ProtostuffUtil.serializer(msg);
			ByteBuf encoded = ctx.alloc().buffer(data.length+4);
			encoded.writeInt(data.length);
	        encoded.writeBytes(data);
	        ctx.write(encoded, promise);
		}
	}
	
	

}
