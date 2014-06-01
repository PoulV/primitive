package org.primitive.webdriverencapsulations.components.bydefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.primitive.proxy.EnhancedProxyFactory;

public final class ComponentFactory {
	private ComponentFactory(){
		super();
	}
	
	public static <T extends WebdriverInterfaceImplementor> T getComponent(
			Class<T> required, final WebDriver driver, Class<?>[] types,
			Object[] args) {
		List<Class<?>> typeList = new ArrayList<Class<?>>() {
			private static final long serialVersionUID = 1L;
			{
				add(WebDriver.class);
			}
		};
		typeList.addAll(Arrays.asList(types));

		List<Object> valueList = new ArrayList<Object>() {
			private static final long serialVersionUID = 1L;

			{
				add(driver);
			}
		};
		return EnhancedProxyFactory.getProxy(required,
				typeList.toArray(new Class<?>[] {}),
				valueList.toArray(new Object[] {}), new ComponentInterceptor());
	}
	
	public static <T extends WebdriverInterfaceImplementor> T getComponent(
			Class<T> required, final WebDriver driver){
		return getComponent(required, driver, new Class<?>[]{}, new Object[] {});
	}
}
