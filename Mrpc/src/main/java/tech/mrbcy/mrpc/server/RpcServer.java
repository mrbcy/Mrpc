package tech.mrbcy.mrpc.server;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import tech.mrbcy.mrpc.common.RpcDecoder;
import tech.mrbcy.mrpc.common.RpcEncoder;
import tech.mrbcy.mrpc.common.RpcRequest;
import tech.mrbcy.mrpc.common.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RpcServer implements ApplicationContextAware, InitializingBean {
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	private String serverAddress;
	private ServerRegister serverRegister;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RpcServer.class);
	
	private String nodePath = "ServiceImplServer";

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public RpcServer(String serverAddress, ServerRegister serverRegister){
    	this.serverAddress = serverAddress;
    	this.serverRegister = serverRegister;
    }

	public void afterPropertiesSet() throws Exception {
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
	                     ch.pipeline().addLast(new RpcServerHandler(handlerMap));     // IN-2
	                 }
	             })
	             .option(ChannelOption.SO_BACKLOG, 128)          
	             .childOption(ChannelOption.SO_KEEPALIVE, true); 

	            String[] array = serverAddress.split(":");
				String host = array[0];
				int port = Integer.parseInt(array[1]);
	            // Bind and start to accept incoming connections.
	            ChannelFuture f = b.bind(host,port).sync(); 

	            LOGGER.debug("server started on port {}", port);
	            System.out.println("server started on port: " + port);
	            
	            if(serverRegister != null){
	            	serverRegister.registServer(nodePath, serverAddress);
	            }
	            
	            f.channel().closeFuture().sync();
	        } finally {
	            workerGroup.shutdownGracefully();
	            bossGroup.shutdownGracefully();
	        }
		
	}

	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		Map<String, Object> serviceBeanMap = ctx
				.getBeansWithAnnotation(RpcService.class);
		if (MapUtils.isNotEmpty(serviceBeanMap)) {
			for (Object serviceBean : serviceBeanMap.values()) {
				//从业务实现类上的自定义注解中获取到value，从来获取到业务接口的全名
				String interfaceName = serviceBean.getClass()
						.getAnnotation(RpcService.class).value();
				handlerMap .put(interfaceName, serviceBean);
			}
		}
		
	}

}
