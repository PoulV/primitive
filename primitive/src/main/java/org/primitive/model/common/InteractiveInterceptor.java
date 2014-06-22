package org.primitive.model.common;

import java.lang.reflect.Method;

import org.primitive.model.abstraction.ModelObjectInterceptor;

import net.sf.cglib.proxy.MethodProxy;

public class InteractiveInterceptor extends ModelObjectInterceptor {

	public InteractiveInterceptor() {
		super();
	}

	/**
	 * Interceptor that sets focus on pages to interact with.
	 */
	@Override
	public synchronized Object intercept(Object funcPart, Method method,
			Object[] args, MethodProxy methodProxy) throws Throwable {
		if (method.isAnnotationPresent(FunctionalPart.InteractiveMethod.class)) { 
			// if there are actions with a page 
			((FunctionalPart) funcPart).switchToMe();
		}
		return super.intercept(funcPart, method, args, methodProxy);
	}
}
