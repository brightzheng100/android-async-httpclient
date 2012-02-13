package bright.zheng.android.httpclient;

import org.apache.http.HttpRequest;

/**
 * Input holder wrapper
 * 
 * @author bright_zheng
 *
 */
public class InputHolder<E>{
	private HttpRequest request;
	private ResponseCallback<E> callback;
	
	public InputHolder(HttpRequest request, ResponseCallback<E> callback){
		this.request = request;
		this.callback = callback;
	}
	
	
	public HttpRequest getRequest() {
		return request;
	}

	public ResponseCallback<E> getCallback() {
		return callback;
	}
}
