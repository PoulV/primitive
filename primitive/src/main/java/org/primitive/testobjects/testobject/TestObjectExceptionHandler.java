package org.primitive.testobjects.testobject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.sf.cglib.proxy.MethodProxy;
import org.primitive.exceptions.BehaviorException;
import org.primitive.interfaces.ITestObjectExceptionHandler;

/**
 * @author s.tihomirov
 * this is an abstract class. It's inheritors should handle exceptions of defined types
 */
public abstract class TestObjectExceptionHandler implements ITestObjectExceptionHandler{
	protected List<Class<? extends Throwable>> throwableList = new ArrayList<Class<? extends Throwable>>();
	
	public TestObjectExceptionHandler(Class<? extends Throwable> tClass)
	{
		throwableList.add(tClass);
	}
	
	public TestObjectExceptionHandler(List<Class<? extends Throwable>> tClassList)
	{
		throwableList.addAll(tClassList);
	}
	
	@Override
	public abstract Object handleException(Object object, Method originalMethod, MethodProxy methodProxy, Object[] args, Throwable t) throws BehaviorException, Throwable;

}
