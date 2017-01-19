package assignment.util;

import com.google.gson.Gson;

public class JsonUtils {
	private static final Gson gson = new Gson();
	public static String toJson(Object obj){
		return gson.toJson(obj);
	}
	
	public static <T> T fromJson(String json, Class<T> classOfT){
		return gson.fromJson(json, classOfT);
	}
}
