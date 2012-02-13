package bright.zheng.android.httpclient;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONTokener;

import android.util.Log;
import bright.zheng.android.Constants;


/**
 * A handler wrapper for bridging the response and the callback. 
 * 
 * @author bright_zheng
 *
 */
public class AsyncResponseHandler{
	private Type type;
	
	@SuppressWarnings("unchecked")
	public <E> void onResponseReceived(HttpEntity response, ResponseCallback<E> callback){
		this.type = getType(getGenericClass(callback.getClass()));
		Object genericResponse = null;
		try {
			switch(this.type){
		        case JSONArray:{
		        	String responseBody = EntityUtils.toString(response);	
		        	Log.i(Constants.TAG, "Return JSON String: " + responseBody);
		        	if(responseBody!=null && responseBody.trim().length()>0){
		        		genericResponse = new JSONTokener(responseBody).nextValue();
		        	}
		        	break;
		        }
		        case JSONObject:{
		        	String responseBody = EntityUtils.toString(response);	
		        	Log.i(Constants.TAG, "Return JSON String: " + responseBody);
		        	if(responseBody!=null && responseBody.trim().length()>0){
		        		genericResponse = new JSONTokener(responseBody).nextValue();
		        	}
		        	break;
		        }
		        case InputStream:{
		        	genericResponse = response.getContent();
		        	break;
		        }
		        default:{
		        	genericResponse = EntityUtils.toString(response);
		        }         
			}
			
			callback.onSuccess((E) genericResponse);
			
	    } catch(IOException e) {
	    	callback.onFailure(e);
	    } catch (JSONException e) {
	    	callback.onFailure(e);
		}	
	}
	
	public <E> void onResponseReceived(Throwable response, ResponseCallback<E> callback){
		callback.onFailure(response);
	}

	private Class<?> getGenericClass(Class<?> cls) { 
        ParameterizedType parameterizedType = ((ParameterizedType) cls.getGenericInterfaces()[0]); 
        Object genericClass = parameterizedType.getActualTypeArguments()[0]; 
        if (genericClass instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericClass).getRawType(); 
        } else if (genericClass instanceof GenericArrayType) {
            return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType(); 
        } else { 
            return (Class<?>) genericClass; 
        } 
    }
	
	private Type getType(Class<?> clazz){
		Type result = Type.String;
		for (Type type: Type.values()){
			if (type.value.equals(clazz.getSimpleName())){
				result = type;
				break;
			}
		}
		return result;
	}
	
	private enum Type {
		JSONArray ("JSONArray"),
		JSONObject ("JSONObject"),
		InputStream ("InputStream"),
		String ("String");
		
		private final String value;
		
		Type(String value){
			this.value = value;
		}
	}
}