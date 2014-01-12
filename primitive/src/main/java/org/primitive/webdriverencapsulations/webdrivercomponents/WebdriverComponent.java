package org.primitive.webdriverencapsulations.webdrivercomponents;

import org.openqa.selenium.WebDriver;

/**
 * @author s.tihomirov
 * For any class that executes specific actions using webdriver
 */
public abstract class WebdriverComponent {
	
	protected WebDriver driver;
	
	public WebdriverComponent(WebDriver driver)	{
		this.driver = driver;
	}
	
}
