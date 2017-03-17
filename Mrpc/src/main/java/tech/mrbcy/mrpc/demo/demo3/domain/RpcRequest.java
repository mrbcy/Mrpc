package tech.mrbcy.mrpc.demo.demo3.domain;

import java.util.Arrays;

public class RpcRequest {
	private String id; // 请求id
	private String methodName; // 方法名
	private Class<?>[] paramTypes; // 方法参数类型
	private Object[] args; // 调用参数
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public Class<?>[] getParamTypes() {
		return paramTypes;
	}
	public void setParamTypes(Class<?>[] paramTypes) {
		this.paramTypes = paramTypes;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	@Override
	public String toString() {
		return "RpcRequest [id=" + id + ", methodName=" + methodName
				+ ", paramTypes=" + Arrays.toString(paramTypes) + ", args="
				+ Arrays.toString(args) + "]";
	}
	
	
}
