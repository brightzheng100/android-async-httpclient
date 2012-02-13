package bright.zheng.android.httpclient;

import org.apache.http.HttpEntity;

/**
 * Output holder wrapper
 * 
 * @author bright_zheng
 *
 */
public class OutputHolder<E>{
	private HttpEntity response;
	private Throwable exception;
	private ResponseCallback<E> callback;
	
	public OutputHolder(HttpEntity response, ResponseCallback<E> callback){
		this.response = response;
		this.callback = callback;
	}
	
	public OutputHolder(Throwable exception, ResponseCallback<E> callback){
		this.exception = exception;
		this.callback = callback;
	}

	public HttpEntity getResponse() {
		return response;
	}

	public Throwable getException() {
		return exception;
	}
	
	public ResponseCallback<E> getCallback() {
		return callback;
	}
	
}