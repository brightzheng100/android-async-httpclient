package bright.zheng.android.httpclient;

import java.lang.reflect.ParameterizedType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;

import android.os.AsyncTask;
import android.util.Log;
import bright.zheng.android.Constants;

/**
 * AsyncHttpSender is the AsyncTask implementation
 * 
 * @author bright_zheng
 *
 */
public class AsyncHttpSender<E> extends AsyncTask<InputHolder<E>, Void, OutputHolder<E>> {

	@Override
	protected OutputHolder<E> doInBackground(InputHolder<E>... params) {
		HttpEntity entity = null;
		InputHolder<E> input = params[0];
		try {
			HttpResponse response = AsyncHttpClient.getClient().execute((HttpUriRequest) input.getRequest());
			StatusLine status = response.getStatusLine();
			
	        if(status.getStatusCode() >= 300) {
	        	return new OutputHolder<E>(
	        			new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()),
	        			input.getCallback());
	        }
	        
			entity = response.getEntity();
			Log.i(Constants.TAG, "isChunked:" + entity.isChunked());
			entity = new BufferedHttpEntity(entity);
		} catch (Exception e) {
			Log.e(Constants.TAG, e.getMessage(), e);
			return new OutputHolder<E>(e, input.getCallback());
		}
		return new OutputHolder<E>(entity, input.getCallback());
	}
	
	@Override
    protected void onPreExecute(){
		Log.i(Constants.TAG, "AsyncHttpSender.onPreExecute()");
		super.onPreExecute();
	}
	
	@Override
	protected void onPostExecute(OutputHolder<E> result) {
		Log.i(Constants.TAG, "AsyncHttpSender.onPostExecute()");
		super.onPostExecute(result);
		
		if(isCancelled()){
			Log.i(Constants.TAG, "AsyncHttpSender.onPostExecute(): isCancelled() is true");
			return; //Canceled, do nothing
		}
		
		ResponseCallback<E> callback = result.getCallback();
		HttpEntity response = result.getResponse();
		Throwable exception = result.getException();
		
		AsyncResponseHandler handler = new AsyncResponseHandler();
		if(response!=null){
			Log.i(Constants.TAG, "AsyncResponseHandler.onResponseReceived()");
			//callback.onSuccess(response);
			handler.onResponseReceived(response, callback);
		}else{
			Log.i(Constants.TAG, "AsyncResponseHandler.onResponseReceived()");
			//callback.onFailure(exception);
			handler.onResponseReceived(exception, callback);
		}
	}
	
	public Class<?> getClazz() {
        ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericSuperclass();
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }
	
	@Override
    protected void onCancelled(){
		Log.i(Constants.TAG, "AsyncHttpSender.onCancelled()");
		super.onCancelled();
		//this.isCancelled = true;
	}
}