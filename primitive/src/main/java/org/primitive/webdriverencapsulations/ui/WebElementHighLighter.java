package org.primitive.webdriverencapsulations.ui;

import java.awt.Color;
import java.util.logging.Level;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.primitive.configuration.Configuration;
import org.primitive.interfaces.IConfigurable;
import org.primitive.interfaces.IDestroyable;
import org.primitive.interfaces.IWebElementHighlighter;
import org.primitive.logging.Log;
import org.primitive.logging.Photographer;
import org.primitive.logging.eLogColors;

/**
 * @author s.tihomirov
 * it can highlight elements and do screenshots
 */	
public class WebElementHighLighter implements IConfigurable,
		IWebElementHighlighter, IDestroyable {

	//is this doing screenshots
	private boolean toDoScreenShots; 
	private final boolean isDoingScreenShotsByDefault = true;
	
	private String getOriginalStyle(WebElement elementToBeHiglighted)
	{
		return elementToBeHiglighted.getAttribute("style");
	}
	
	private void execDecorativeScript(JavascriptExecutor scriptExecutor, WebElement element, String script) throws InterruptedException
	{
		try
		{
			scriptExecutor.executeScript(script,  element);
		}
		catch (ClassCastException e)
		{
			scriptExecutor.executeScript(script,  ((WrapsElement) element).getWrappedElement());
		}
		Thread.sleep(100);
	}
	
	private void setNewColor(JavascriptExecutor scriptExecutor, WebElement elementToBeHiglighted, String colorExpression)
	{
		try {
			execDecorativeScript(scriptExecutor, elementToBeHiglighted, "arguments[0].style.border = '" + colorExpression + "'");
		} catch (InterruptedException|StaleElementReferenceException e) {}
	}
	
	private void setStyle(JavascriptExecutor scriptExecutor, WebElement elementToBeHiglighted, String style)
	{
		try {
			execDecorativeScript(scriptExecutor, elementToBeHiglighted, "arguments[0].setAttribute('style', '" + style + "');"); 
		}  catch (InterruptedException|StaleElementReferenceException e) {}
	}
	
	private void highlightelement(WebDriver driver, WebElement webElement, Color color, Level LogLevel, String Comment)
	{
		String originalStyle = getOriginalStyle(webElement);
		setNewColor((JavascriptExecutor) driver, webElement, "2px solid rgb("+ Integer.toString(color.getRed()) +  ","+Integer.toString(color.getGreen())+","+Integer.toString(color.getBlue())+")");
		if (toDoScreenShots)
		{	
			Photographer.takeAPictureForLog(driver, LogLevel, Comment);
		}	
		else
		{
			Log.log(LogLevel, Comment);
		}
		setStyle((JavascriptExecutor) driver, webElement, originalStyle);
	}
	
	@Override
	public synchronized void highlight(WebDriver driver, WebElement webElement,
			Color highlight, Level LogLevel, String Comment) {
		highlightelement(driver, webElement, highlight, LogLevel, Comment);
	}

	@Override
	public synchronized void highlightAsFine(WebDriver driver, WebElement webElement,
			Color highlight, String Comment) {
		highlightelement(driver, webElement, highlight, Level.FINE, Comment);
	}

	@Override
	public synchronized void highlightAsFine(WebDriver driver, WebElement webElement,
			String Comment) {
		highlightelement(driver, webElement, eLogColors.DEBUGCOLOR.getStateColor(), Level.FINE, Comment);
	}

	@Override
	public synchronized void highlightAsInfo(WebDriver driver, WebElement webElement,
			Color highlight, String Comment) {
		highlightelement(driver, webElement, highlight, Level.INFO, Comment);
	}

	@Override
	public synchronized void highlightAsInfo(WebDriver driver, WebElement webElement,
			String Comment) {
		highlightelement(driver, webElement, eLogColors.CORRECTSTATECOLOR.getStateColor(), Level.INFO, Comment);
	}

	@Override
	public synchronized void highlightAsSevere(WebDriver driver, WebElement webElement,
			Color highlight, String Comment) {
		highlightelement(driver, webElement, highlight, Level.SEVERE, Comment);
	}

	@Override
	public synchronized void highlightAsSevere(WebDriver driver, WebElement webElement,
			String Comment) {
		highlightelement(driver, webElement, eLogColors.SEVERESTATECOLOR.getStateColor(), Level.SEVERE, Comment);
	}

	@Override
	public synchronized void highlightAsWarning(WebDriver driver, WebElement webElement,
			Color highlight, String Comment) {
		highlightelement(driver, webElement, highlight, Level.WARNING, Comment);
	}

	@Override
	public synchronized void highlightAsWarning(WebDriver driver, WebElement webElement,
			String Comment) {
		highlightelement(driver, webElement, eLogColors.WARNSTATECOLOR.getStateColor(), Level.WARNING, Comment);
	}

	@Override
	public synchronized void resetAccordingTo(Configuration config) {
		Boolean toDoScreenShots = config.getScreenShots().getToDoScreenShotsOnElementHighLighting();
		if (toDoScreenShots==null)
		{
			this.toDoScreenShots = isDoingScreenShotsByDefault;
		}
		else
		{
			this.toDoScreenShots = toDoScreenShots;
		}
	}

	@Override
	public void destroy() {
		try {
			this.finalize();
		} catch (Throwable e) {}		
	}

}