package br.uniriotec.vitor.padilha.dissertacao.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {

	@SuppressWarnings("rawtypes")
	public static Map<String,Method> getClassPublicMethods(Class clazz){
		Map<String,Method> metodosList = new HashMap<String, Method>();		
		for(Method metodo:clazz.getMethods()) {
			if(Modifier.isPublic(metodo.getModifiers()))
				metodosList.put(metodo.getName(), metodo);
		}
			
		return metodosList;
	}
}
