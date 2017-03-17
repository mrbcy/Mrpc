package tech.mrbcy.mrpc.client;

import java.net.InetSocketAddress;

public interface ServerSwitchListener {
	/**
	 * 切换到新的服务器，一般在之前获取的服务器不可用的情况下被调用
	 * @param newServerAddr 新的服务器地址，如果没有可用的服务器则为null
	 */
	void serverSwitched(InetSocketAddress newServerAddr);
}
