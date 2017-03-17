package tech.mrbcy.mrpc.demo.demo3.server;

import tech.mrbcy.mrpc.demo.demo3.common.RpcDecoder;
import tech.mrbcy.mrpc.demo.demo3.common.RpcEncoder;
import tech.mrbcy.mrpc.demo.demo3.domain.RpcRequest;
import tech.mrbcy.mrpc.demo.demo3.domain.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DemoRpcServer {
	 private int port;

    public DemoRpcServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); 
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); 
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) 
             .childHandler(new ChannelInitializer<SocketChannel>() { 
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 ch.pipeline().addLast(new RpcEncoder(RpcResponse.class)); // OUT-1
                	 ch.pipeline().addLast(new RpcDecoder(RpcRequest.class)); // IN-1
                     ch.pipeline().addLast(new DemoRpcServerHandler());     // IN-2
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          
             .childOption(ChannelOption.SO_KEEPALIVE, true); 

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); 

            System.out.println("server started at port:" + port);
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
