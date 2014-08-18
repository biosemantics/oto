package edu.arizona.biosemantics.oto.steps.shared.rpc;

import java.io.Serializable;

/**
 * Fails when authentication fails or when there was an internal error in the processing of the RPC, such as a caught Exception.
 * Message details what the error was or that authentication failed.
 * Data contains the data that the caller requested 
 * @author rodenhausen
 *
 * @param <T>
 */
public class RPCResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean succeeded;
	private String message="";
	private T data;
	
	public RPCResult() { }
	
	public RPCResult(boolean succeeded, String message) {
		super();
		this.succeeded = succeeded;
		this.message = message;
	}
	
	public RPCResult(boolean succeeded, String message, T data) {
		super();
		this.succeeded = succeeded;
		this.message = message;
		this.data = data;
	}
	
	public RPCResult(boolean succeeded) {
		this.succeeded = succeeded;
		this.message = "";
	}
	
	public RPCResult(boolean succeeded, T data) {
		this.succeeded = succeeded;
		this.message = "";
		this.data = data;
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public boolean hasData() {
		return data != null;
	}
}
