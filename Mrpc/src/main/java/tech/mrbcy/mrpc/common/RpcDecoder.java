package tech.mrbcy.mrpc.common;

import java.util.List;

import tech.mrbcy.mrpc.demo.demo1.ProtostuffUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDecoder extends ByteToMessageDecoder{

	private Class clazz;
	public RpcDecoder(Class clazz){
		this.clazz = clazz;
	}
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		// 前4字节记录长度
		if (in.readableBytes() < 4) {
            return;
        }
		in.markReaderIndex();
		int dataLength = in.readInt();
        if (dataLength < 0) {
        	// 出错了
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        
        //将ByteBuf转换为byte[]
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        //将data转换成object
        @SuppressWarnings("unchecked")
		Object obj = ProtostuffUtil.deserializer(data, clazz);
        out.add(obj);
	}
	

}
