package tech.mrbcy.mrpc.demo.demo2;

import java.util.List;

public interface ServerChangeListener {
	/**
	 * 服务器列表发生变化
	 * @param servers
	 */
	void onChange(List<String> servers);
}
