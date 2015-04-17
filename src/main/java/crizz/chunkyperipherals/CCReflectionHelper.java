package crizz.chunkyperipherals;

import java.lang.reflect.InvocationTargetException;

public class CCReflectionHelper
{
	public static class CCClassNotFoundException extends RuntimeException
	{}
	
	public static class CCMethodNotFoundException extends RuntimeException
	{}
	
	private static Class ccMainClass;
	
	public static Class getMainCCClass()
	{
		if(ccMainClass==null)
		{
			try {
				ccMainClass = Class.forName("dan200.computercraft.ComputerCraft");
			} catch (ClassNotFoundException e)
			{
				ChunkyPeripherals.logger.error("Computercraft class not found");
				e.printStackTrace();
				throw new CCClassNotFoundException();
			}
		}
		return ccMainClass;
	}
	
	public static Object runMainCCClassMethod(String methodName,  Object... parameters)
	{
		getMainCCClass();
		return invokeStaticMethod(ccMainClass, methodName,true, parameters);
	}
	
	public static Object invokeStaticMethod(Class classe, String methodName, boolean preserveInt, Object... parameters)
	{
		Class<?>[] types = new Class<?>[parameters.length];
		
		for(int i=0;i<parameters.length;i++)
		{
			types[i]=parameters[i].getClass();
			if(preserveInt && types[i]==Integer.class)
				types[i]=int.class;
		}
		
		Object ret = null;
		try {
			ret = classe.getMethod(methodName, types).invoke(null, parameters);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		}
		return ret;
	}
	
	public static Object invokeMethod(Object thisObject, boolean preserveInt, String methodName, Object... parameters)
	{
		Class c = thisObject.getClass();
		
		Class<?>[] types =  new Class<?>[parameters.length];
		for(int i=0;i<parameters.length;i++)
		{
			types[i]=parameters[i].getClass();
			if(preserveInt && types[i]==Integer.class)
				types[i]=int.class;
		}		
		
		Object ret = null;
		
		try {
			
			ret = c.getMethod(methodName, types).invoke(thisObject, parameters);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		}
		return ret;
	}
	
	public static Object invokeMethod(Object thisObject, boolean preserveInt, String methodName, Class<?>[] parameterTypes, Object... parameters)
	{
		Class c = thisObject.getClass();
		
			
		
		Object ret = null;
		
		try {
			
			ret = c.getMethod(methodName, parameterTypes).invoke(thisObject, parameters);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		}
		return ret;
	}
	
	
	public static Object ccObjectNewInstance(String classString, boolean preserveInt, Object... constructorParameters)
	{
		classString = "dan200.computercraft."+classString;
		
		Class<?>[] types =  new Class<?>[constructorParameters.length];
		for(int i=0;i<constructorParameters.length;i++)
		{
			types[i]=constructorParameters[i].getClass();
			if(preserveInt && types[i]==Integer.class)
				types[i]=int.class;
		}
		
		Class c = null;
		try {
			c = Class.forName(classString);
		} catch (ClassNotFoundException e)
		{
			ChunkyPeripherals.logger.error("Computercraft class not found");
			e.printStackTrace();
			throw new CCClassNotFoundException();
		}
		
		Object obj = null;
		try {
			
			obj = c.getConstructor(types).newInstance(constructorParameters);
			
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new CCMethodNotFoundException();
		}
		return obj;
	}
	
	public static Class getCCClass(String className)
	{
		className = "dan200.computercraft."+className;
		
		
		
		Class c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e)
		{
			ChunkyPeripherals.logger.error("Computercraft class not found");
			e.printStackTrace();
			throw new CCClassNotFoundException();
		}
		return c;
	}
}
