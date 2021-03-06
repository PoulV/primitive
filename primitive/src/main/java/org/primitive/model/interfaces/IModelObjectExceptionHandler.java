package org.primitive.model.interfaces;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
/**
 * @author s.tihomirov
 *
 */
public interface IModelObjectExceptionHandler 
{
	public Object handleException(Object object, Method originalMethod, MethodProxy methodProxy, Object[] args, Throwable t) throws Throwable;
}
