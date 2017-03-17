package tech.mrbcy.mrpc.common;

public class RpcResponse {
	private String id;
	private boolean isSuccess = true;
	private Throwable error;
	private Object result;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Throwable getError() {
		return error;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "RpcResponse [id=" + id + ", isSuccess=" + isSuccess
				+ ", error=" + error + ", result=" + result + "]";
	}
	
	
}
