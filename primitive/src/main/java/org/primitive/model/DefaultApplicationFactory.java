package org.primitive.model;

import java.lang.reflect.Constructor;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.primitive.configuration.Configuration;
import org.primitive.configuration.webdriver.ESupportedDrivers;
import org.primitive.model.abstraction.ModelObjectInterceptor;
import org.primitive.model.interfaces.IDecomposable;
import org.primitive.proxy.EnhancedProxyFactory;
import org.primitive.webdriverencapsulations.Handle;
import org.primitive.webdriverencapsulations.Manager;
import org.primitive.webdriverencapsulations.WebDriverEncapsulation;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 *Utility class that contains methods which create {@link Application} instances
 */
class DefaultApplicationFactory {

	/**
	 * An interceptor for {@link FunctionalPart} inheritor defined by user.
	 * Defined for each thread
	 */
	private static ThreadLocal<Class<? extends MethodInterceptor>> definedInteractiveInterceptor = 
			new ThreadLocal<Class<? extends MethodInterceptor>>();
	/**
	 * An interceptor for {@link Application} inheritor defined by user. Defined for
	 * each thread
	 */
	private final static ThreadLocal<Class<? extends MethodInterceptor>> definedInterceptorForEntities = 
			new ThreadLocal<Class<? extends MethodInterceptor>>();

	DefaultApplicationFactory() {
		super();
	}

	/**
	 * Resets iterceptor class for {@link Application}
	 */
	public static void resetApplicationInterceptor(Class<? extends ModelObjectInterceptor> interceptorClass){
		resetInterceptor(definedInterceptorForEntities, interceptorClass);
	}

	/**
	 * Resets iterceptor class for {@link FunctionalPart}
	 */
	public static void resetInteractiveInterceptor(Class<? extends InteractiveInterceptor> interceptorClass){
		resetInterceptor(definedInteractiveInterceptor, interceptorClass);
	}

	private static void resetInterceptor(
			ThreadLocal<Class<? extends MethodInterceptor>> to,
			Class<? extends MethodInterceptor> interceptorClass) {
		to.set(interceptorClass);
	}

	/**
	 *  Creation of any decomposable part of application
	 */
	static <T extends IDecomposable> T get(Class<T> partClass,
			Class<?>[] paramClasses, Object[] paramValues){
		T decomposable = EnhancedProxyFactory.getProxy(partClass,
				paramClasses, paramValues, getInteractiveInterceptor());
		return decomposable;
	}

	private static <T extends MethodInterceptor> T getInteractiveInterceptor(){
		return getInterceptorFromThreadLocal(
				definedInteractiveInterceptor, InteractiveInterceptor.class);
	}

	@SuppressWarnings("unchecked")
	private static <T extends MethodInterceptor> T  getInterceptorFromThreadLocal(
			ThreadLocal<Class<? extends MethodInterceptor>> from,
			Class<? extends MethodInterceptor> defaultInterceptorClass) {
		try {
			if (from.get() == null) {
				return (T) defaultInterceptorClass.newInstance();
			}
			return (T) from.get().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static Handle getTheFirstHandle(Class<? extends Manager> handleManagerClass,
			WebDriverEncapsulation wdeInstance) {
		try {
			Constructor<?> c = handleManagerClass
					.getConstructor(new Class<?>[] { WebDriverEncapsulation.class });
			Manager m = (Manager) c.newInstance(new Object[] { wdeInstance });

			return m.getByInex(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static Handle getTheFirstHandle(Class<? extends Manager> handleManagerClass,
			Class<?>[] wdEncapsulationParams, Object[] wdEncapsulationParamVals) {
		try {
			Constructor<?> wdeC = WebDriverEncapsulation.class
					.getConstructor(wdEncapsulationParams);
			WebDriverEncapsulation wdeInstance = (WebDriverEncapsulation) wdeC
					.newInstance(wdEncapsulationParamVals);
			return getTheFirstHandle(handleManagerClass, wdeInstance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static <T extends MethodInterceptor> T getAppInterceptor() {
		return  getInterceptorFromThreadLocal(
				definedInterceptorForEntities, ModelObjectInterceptor.class);
	}
	
	/**
	 * Common method that creates an instance of any application 
	 * with default configuration
	 */
	static <T extends Application> T getApplication(Class<? extends Manager> handleManagerClass, 
			Class<T> appClass){
		Handle h = getTheFirstHandle(handleManagerClass,
				new Class<?>[] { Configuration.class },
				new Object[] { Configuration.byDefault });
		return EnhancedProxyFactory.getProxy(appClass,
				new Class<?>[] { h.getClass() }, new Object[] { h },
				getAppInterceptor());
	}

	 /** Common method that creates an instance of any application 
	 * with defined configuration
	 */
	static <T extends Application> T getApplication(Class<? extends Manager> handleManagerClass, Class<T> appClass,
			Configuration config) {
		Handle h = getTheFirstHandle(handleManagerClass,
				new Class<?>[] { Configuration.class }, new Object[] { config });
		return EnhancedProxyFactory.getProxy(appClass,
				new Class<?>[] { h.getClass() }, new Object[] { h },
				getAppInterceptor());
	}

	 /** Common method that creates an instance of any application 
	 * with defined webdriver
	 */
	static <T extends Application> T getApplication(Class<? extends Manager> handleManagerClass, Class<T> appClass,
			ESupportedDrivers supportedDriver){
		Handle h = getTheFirstHandle(handleManagerClass,
				new Class<?>[] { ESupportedDrivers.class },
				new Object[] { supportedDriver });
		return EnhancedProxyFactory.getProxy(appClass,
				new Class<?>[] { h.getClass() }, new Object[] { h },
				getAppInterceptor());
	}
	
	 /** Common method that creates an instance of any application 
	 * with defined webdriver and its capabilities
	 */
	static <T extends Application> T getApplication(
			Class<? extends Manager> handleManagerClass, Class<T> appClass,
			ESupportedDrivers supportedDriver, Capabilities capabilities) {
		Handle h = getTheFirstHandle(handleManagerClass, new Class<?>[] {
				ESupportedDrivers.class, Capabilities.class }, new Object[] {
				supportedDriver, capabilities });
		return EnhancedProxyFactory.getProxy(appClass,
				new Class<?>[] { h.getClass() }, new Object[] { h },
				getAppInterceptor());
	}
	
	 /** Common method that creates an instance of any application 
	 * with defined webdriver, capabilities and URL to remote server
	 */
	static <T extends Application> T getApplication(Class<? extends Manager> handleManagerClass, Class<T> appClass,
			ESupportedDrivers supportedDriver, Capabilities capabilities,
			URL remoteAddress) {
		Handle h = getTheFirstHandle(handleManagerClass, new Class<?>[] {
				ESupportedDrivers.class, Capabilities.class, URL.class }, new Object[] {
				supportedDriver, capabilities, remoteAddress});
		return EnhancedProxyFactory.getProxy(appClass,
				new Class<?>[] { h.getClass() }, new Object[] { h },
				getAppInterceptor());
	}
	
	 /** Common method that creates an instance of any application 
	 * with defined webdriver and URL to remote server
	 */
	static <T extends Application> T getApplication(Class<? extends Manager> handleManagerClass, Class<T> appClass,
			ESupportedDrivers supportedDriver, 
			URL remoteAddress) {
		Handle h = getTheFirstHandle(handleManagerClass, new Class<?>[] {
				ESupportedDrivers.class, URL.class }, new Object[] {
				supportedDriver, remoteAddress});
		return EnhancedProxyFactory.getProxy(appClass,
				new Class<?>[] { h.getClass() }, new Object[] { h },
				getAppInterceptor());
	}
	
	 /** Common method that creates an instance of any application 
	 * with externally instantiated {@link WebDriverEncapsulation}
	 */
	static <T extends Application> T getApplication(Class<? extends Manager> handleManagerClass, Class<T> appClass,
			WebDriverEncapsulation wdEncapsulation) {
		Handle h = getTheFirstHandle(handleManagerClass, wdEncapsulation);
		return EnhancedProxyFactory.getProxy(appClass,
				new Class<?>[] { h.getClass() }, new Object[] { h },
				getAppInterceptor());
	}

}
