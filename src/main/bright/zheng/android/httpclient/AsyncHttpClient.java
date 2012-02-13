package bright.zheng.android.httpclient;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import bright.zheng.android.Constants;

/**
 * 
 * AsyncHttpClient is the client tools which provides static methods.
 * 
 * @author bright_zheng
 *
 */
public class AsyncHttpClient<E> {
	private static DefaultHttpClient httpClient;
	
	private static ConcurrentHashMap<Activity,AsyncHttpSender<?>> 
		tasks = new ConcurrentHashMap<Activity,AsyncHttpSender<?>>();
		
	public static <E> void sendRequest(
			final Activity currentActitity,
			final HttpRequest request,
			ResponseCallback<E> callback) {
		
		sendRequest(
				currentActitity, 
				request, 
				callback, 
				Constants.CONNECTION_TIMEOUT, 
				Constants.SOCKET_TIMEOUT);
	}
	
	@SuppressWarnings("unchecked")
	public static <E> void sendRequest(
			final Activity currentActitity,
			final HttpRequest request,
			ResponseCallback<E> callback,
			int timeoutConnection,
			int timeoutSocket) {
		
		InputHolder<E> input = new InputHolder<E>(request, callback);
		AsyncHttpSender<E> sender = new AsyncHttpSender<E>();
		sender.execute(input);
		tasks.put(currentActitity, sender);
	}
	
	/**
	 * Cancel the async tasks linked to particular <code>Activity</code> 
	 * 
	 * @param currentActitity
	 */
	public static void cancelRequest(final Activity currentActitity){
		if(tasks==null || tasks.size()==0) return;
		for (Activity key : tasks.keySet()) {
		    if(currentActitity == key){
		    	AsyncTask<?,?,?> task = tasks.get(key);
		    	if(task.getStatus()!=null && task.getStatus()!=AsyncTask.Status.FINISHED){
			    	Log.i(Constants.TAG, "AsyncTask of " + task + " cancelled.");
		    		task.cancel(true);
		    	}
		    	tasks.remove(key);
		    }
		}
	}
 
	@SuppressWarnings("deprecation")
	public static synchronized HttpClient getClient() {
		if (httpClient == null){			
			//use following code to solve Adapter is detached error
			//refer: http://stackoverflow.com/questions/5317882/android-handling-back-button-during-asynctask
			BasicHttpParams params = new BasicHttpParams();
			
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
			schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
			
			// Set the timeout in milliseconds until a connection is established.
			HttpConnectionParams.setConnectionTimeout(params, Constants.CONNECTION_TIMEOUT);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(params, Constants.SOCKET_TIMEOUT);
			
			ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
			httpClient = new DefaultHttpClient(cm, params);	
		}
		return httpClient;
	}
 
}