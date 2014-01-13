package org.primitive.webdriverencapsulations.eventlisteners;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.primitive.logging.Log;
import org.primitive.webdriverencapsulations.ui.WebElementHighLighter;

public final class DefaultWebdriverListener implements IExtendedWebDriverEventListener {

	private WebElementHighLighter highLighter = new WebElementHighLighter();
	
	@Override
	public void afterChangeValueOf(WebElement arg0, WebDriver arg1) {
		highLighter.highlightAsInfo(arg1, arg0, "Element with value that is changed: " + elementDescription(arg0));
	}

	@Override
	public void afterClickOn(WebElement arg0, WebDriver arg1) 
	{
		Log.message("Click on element has been successfully performed!");
	}

	@Override
	public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		Log.debug("Searching for web element has been finished. Locator is " + arg0.toString() + ". Element is:" + elementDescription(arg1));
	}

	@Override
	public void afterNavigateBack(WebDriver arg0) {
		Log.message("Current URL is  " + arg0.getCurrentUrl());
	}

	@Override
	public void afterNavigateForward(WebDriver arg0) 
	{
		Log.message("Current URL is  " + arg0.getCurrentUrl());
	}
	
	@Override
	public void afterNavigateTo(String arg0, WebDriver arg1) {
		Log.message("Current URL is " + arg1.getCurrentUrl());
	}

	@Override
	public void afterScript(String arg0, WebDriver arg1) {
		Log.debug("Javascript  " + arg0 + " has been executed successfully!");
	}
	
	@Override
	public void beforeChangeValueOf(WebElement arg0, WebDriver arg1) {
		highLighter.highlightAsInfo(arg1, arg0, "Element with value that will be changed: "  + elementDescription(arg0));
	}


	@Override
	public void beforeClickOn(WebElement arg0, WebDriver arg1)
	{
		highLighter.highlightAsInfo(arg1, arg0, "Click will be performed on this element: "  + elementDescription(arg0));
	}

	@Override
	public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		Log.debug("Searching for element by locator " + arg0.toString() + " has been started");
	}

	@Override
	public void beforeNavigateBack(WebDriver arg0) 
	{
		Log.message("Attempt to navigate to previous url. Current url is " + arg0.getCurrentUrl());
	}

	@Override
	public void beforeNavigateForward(WebDriver arg0) 
	{
		Log.message("Attempt to navigate to next url. Current url is " + arg0.getCurrentUrl());
	}

	@Override
	public void beforeNavigateTo(String arg0, WebDriver arg1) {
		Log.message("Attempt to navigate to another url. Required url is " + arg0);
	}

	@Override
	public void beforeScript(String arg0, WebDriver arg1) 
	{
		Log.debug("Javascript execution has been started " + arg0);
	}

	@Override
	public void onException(Throwable arg0, WebDriver arg1)
	{
		Log.debug("An exception has been caught out.", arg0);
	}
	

	@Override
	public void beforeSubmit(WebDriver driver, WebElement element) {
		highLighter.highlightAsInfo(driver, element, "Element that will perform submit: "  + elementDescription(element));			
	}

	@Override
	public void afterSubmit(WebDriver driver, WebElement element) {
		Log.message("Submit has been performed successfully");	
	}

	@Override
	public void beforeAlertDismiss(WebDriver driver, Alert alert) 
	{
		Log.message("Attempt to dismiss the alert...");
		
	}

	@Override
	public void afterAlertDismiss(WebDriver driver, Alert alert) 
	{
		Log.message("Alert has been dismissed");
	}

	@Override
	public void beforeAlertAccept(WebDriver driver, Alert alert) 
	{
		Log.message("Attempt to accept alert...");			
	}

	@Override
	public void afterAlertAccept(WebDriver driver, Alert alert) 
	{
		Log.message("Alert has been accepted");		
	}

	@Override
	public void beforeAlertSendKeys(WebDriver driver, Alert alert, String keys) 
	{
		Log.message("Attemt to send string " + keys + " to alert...");		
	}

	@Override
	public void afterAlertSendKeys(WebDriver driver, Alert alert, String keys) 
	{
		Log.message("String " + keys + " has been sent to alert");			
	}

	@Override
	public void beforeWebDriverSetTimeOut(WebDriver driver, Timeouts timeouts,
			long timeOut, TimeUnit timeUnit) {
		Log.debug("Attempt to set time out. Value is " + Long.toString(timeOut) + " time unit is " + timeUnit.toString());
		
	}

	@Override
	public void afterWebDriverSetTimeOut(WebDriver driver, Timeouts timeouts,
			long timeOut, TimeUnit timeUnit) {
		Log.message("Time out has been set. Value is " + Long.toString(timeOut) + " time unit is " + timeUnit.toString());		
	}

	private String elementDescription(WebElement element) {
		String description = "";
		if (element!=null)
		{	
	        description += "tag:" + String.valueOf(element.getTagName());
	        if (element.getAttribute("id") != null) {
	            description += " id: " + String.valueOf(element.getAttribute("id"));
	        }
	        else if (element.getAttribute("name") != null) {
	            description += " name: " + String.valueOf(element.getAttribute("name"));
	        }
	         
	        description += " ('" + element.getText() + "')";      
		}
        return description;
    }
	
	public void setHighLighter(WebElementHighLighter highLighter)
	{
		this.highLighter = highLighter;
	}

}