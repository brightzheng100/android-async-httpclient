package bright.zheng.android.httpclient;


/**
 * The call back interface for response
 * 
 * @author bright_zheng
 *
 */
public interface ResponseCallback<E> {

	/** Handle successful response */
	public void onSuccess(E response);

	/** Handle exception */
	public void onFailure(Throwable e);
}