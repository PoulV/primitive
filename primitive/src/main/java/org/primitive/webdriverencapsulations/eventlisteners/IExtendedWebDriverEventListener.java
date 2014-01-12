package org.primitive.webdriverencapsulations.eventlisteners;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

/**
 * @author s.tihomirov
 * For some functions that are not declared in WebDriverEventListener
 */
public interface IExtendedWebDriverEventListener extends WebDriverEventListener 
{
	//
	public void beforeSubmit(WebDriver driver, WebElement element);
	//
	public void afterSubmit(WebDriver driver, WebElement element);
	
	public void beforeAlertDismiss(WebDriver driver, Alert alert);
	public void afterAlertDismiss(WebDriver driver, Alert alert);	
	
	public void beforeAlertAccept(WebDriver driver, Alert alert);
	public void afterAlertAccept(WebDriver driver, Alert alert);
	
	public void beforeAlertSendKeys(WebDriver driver, Alert alert, String keys);
	public void afterAlertSendKeys(WebDriver driver, Alert alert, String keys);	
		
	public void beforeWebDriverSetTimeOut(WebDriver driver, Timeouts timeouts, long timeOut, TimeUnit timeUnit);
	public void afterWebDriverSetTimeOut(WebDriver driver, Timeouts timeouts, long timeOut, TimeUnit timeUnit);		
}
