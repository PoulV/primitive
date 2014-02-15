package org.primitive.testobjects;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.primitive.interfaces.IDestroyable;
import org.primitive.testobjects.interfaces.IDecomposable;
import org.primitive.testobjects.interfaces.ITestObjectExceptionHandler;
import org.primitive.webdriverencapsulations.SingleWindow;
import org.primitive.webdriverencapsulations.WebDriverEncapsulation;
import org.primitive.webdriverencapsulations.webdrivercomponents.Awaiting;
import org.primitive.webdriverencapsulations.webdrivercomponents.DriverLogs;
import org.primitive.webdriverencapsulations.webdrivercomponents.ScriptExecutor;

public abstract class TestObject implements IDestroyable, IDecomposable {
	protected final SingleWindow nativeWindow; // browser window that object placed on
	protected final WebDriverEncapsulation driverEncapsulation; // wrapped web driver
															// for situations
															// when it needs to
															// be used

	protected final Awaiting awaiting;
	protected final ScriptExecutor scriptExecutor;
	protected final DriverLogs logs;
	protected final HashSet<TestObjectExceptionHandler> checkedInExceptionHandlers = new HashSet<TestObjectExceptionHandler>();

	// this will be invoked when some exception is caught out
	ITestObjectExceptionHandler exceptionHandler = (ITestObjectExceptionHandler) Proxy
			.newProxyInstance(
					ITestObjectExceptionHandler.class.getClassLoader(),
					new Class[] { ITestObjectExceptionHandler.class },
					new InvocationHandler() {
						public Object invoke(Object proxy, Method method,
								Object[] args) throws Throwable {
							// it needs to know exception
							Throwable t = (Throwable) args[4];
							for (TestObjectExceptionHandler handler : checkedInExceptionHandlers) {
								// it looks for the suitable handler
								if (handler.isThrowableInList(t.getClass())) {
									try {
										return method.invoke(handler, args);
									} catch (Exception e) {
										continue; // it wasn't the suitable
													// handler
									}
								}
							}
							// if there are no suitable handlers
							throw t;
						}
					});

	final List<TestObject> children = Collections
			.synchronizedList(new ArrayList<TestObject>());

	protected TestObject(SingleWindow browserWindow)
			throws ConcstructTestObjectException {
		try {
			nativeWindow = browserWindow;
			driverEncapsulation = nativeWindow.getDriverEncapsulation();
			awaiting = driverEncapsulation.getAwaiting();
			scriptExecutor = driverEncapsulation.getScriptExecutor();
			logs = driverEncapsulation.getLogs();
		} catch (Exception e) {
			throw new ConcstructTestObjectException(
					"Test object form hasn't been constructed. You can get the reason of the error "
							+ " for situation analysis", e);
		}
	}

	@SuppressWarnings("unchecked")
	static <T extends IDecomposable> T getProxy(Class<T> clazz,
			Class<? extends MethodInterceptor> interceptorClass,
			Class<?>[] paramClasses, Object[] paramValues)
			throws ConcstructTestObjectException { // should be closed by child
													// class method
		Enhancer enhancer = new Enhancer();
		MethodInterceptor interceptor = null;
		try {
			interceptor = interceptorClass.newInstance();
			enhancer.setCallback(interceptor);
			enhancer.setSuperclass(clazz);
		} catch (Exception e) {
			throw new ConcstructTestObjectException(e.getMessage(), e);
		}

		T objectToBeTested = (T) enhancer.create(paramClasses, paramValues);
		return objectToBeTested;
	}

	public void destroy() {
		for (TestObject child : children) {
			child.destroy();
		}
		children.clear();
	}

	public void checkInExceptionHandler(
			TestObjectExceptionHandler exceptionHandler) {
		checkedInExceptionHandlers.add(exceptionHandler);
	}

	public void checkOutExceptionHandler(
			TestObjectExceptionHandler exceptionHandler) {
		checkedInExceptionHandlers.remove(exceptionHandler);
	}

	@Override
	public abstract <T extends IDecomposable> T getPart(Class<T> partClass);

	@Override
	public abstract <T extends IDecomposable> T getPart(Class<T> partClass,
			Integer frameIndex);

	@Override
	public abstract <T extends IDecomposable> T getPart(Class<T> partClass,
			String pathToFrame);

	void addChild(TestObject child) {
		children.add(child);
	}

	@Override
	public abstract <T extends IDecomposable> T getPart(Class<T> partClass,
			String pathToFrame, Long timeOutInSec);
}
