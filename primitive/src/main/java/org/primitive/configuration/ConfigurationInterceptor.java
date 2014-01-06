package org.primitive.configuration;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author s.tihomirov
 * it does requests to default configuration when there is no data in specified configuration
 */
class ConfigurationInterceptor implements MethodInterceptor {

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Object result = proxy.invokeSuper(obj, args);
		if ((result==null)&(obj!=Configuration.byDefault))
		{
			result = proxy.invokeSuper(Configuration.byDefault, args);
		}
		return result;
	}

}
