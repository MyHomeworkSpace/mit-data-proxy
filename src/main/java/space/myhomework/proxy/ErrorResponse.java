package space.myhomework.proxy;

public class ErrorResponse {
	public String status;
	public String error;

	public ErrorResponse(String newStatus, String newError) {
		this.status = newStatus;
		this.error = newError;
	}
}