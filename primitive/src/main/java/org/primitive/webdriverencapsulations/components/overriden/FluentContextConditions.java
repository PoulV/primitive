package org.primitive.webdriverencapsulations.components.overriden;

import java.util.ArrayList;
import java.util.Set;

import org.openqa.selenium.ContextAware;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.primitive.webdriverencapsulations.components.WebdriverComponent;

/**
 * Waits for some conditions on native and web-based contexts.
 *
 */
public class FluentContextConditions extends WebdriverComponent {

	public FluentContextConditions(WebDriver driver) {
		super(driver);
	}
	
	private Boolean isContextPresent(final WebDriver from, String context){
		Set<String> contexts = ((ContextAware) from).getContextHandles();
		if (contexts.contains(context)){
			return true;
		}
		return null;
	}
	
	private String getNewContext(final WebDriver from, final Set<String> oldContexts){
		String newContext = null;
		Set<String> newContexts = ((ContextAware) from).getContextHandles();
		if (newContexts.size() > oldContexts.size()) {
			newContexts.removeAll(oldContexts);
			newContext = (String) newContexts.toArray()[0];
			return newContext;
		}
		return newContext;
	}
	
	/**
	 * Waiting for the context is present
	 */
	public ExpectedCondition<Boolean> isContextPresent(final String context){
		return new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver input) {
				return isContextPresent(input, context);
			}			
		};
	}

	/**
	 * Waiting for a new context is appeared
	 */
	public ExpectedCondition<String> newContextIsAppeared() {
		return new ExpectedCondition<String>() {
			Set<String> oldContexts= ((ContextAware) driver).getContextHandles();
	
			public String apply(final WebDriver from) {
				return getNewContext(from, oldContexts);
			}
		};
	}
	
	private String getContextByIndex(final WebDriver from, int contextIndex) {
		Set<String> contexts = ((ContextAware) from).getContextHandles();
		if ((contexts.size() - 1) >= contextIndex) {
			((ContextAware) from).context(contexts.toArray()[contextIndex].toString());
			return new ArrayList<String>(contextIndex).get(contextIndex);
		} else {
			return null;
		}
	}

	/**
	 * Waiting for context is present. Context is defined by index
	 */
	public ExpectedCondition<String> suchContextWithIndexIsPresent(final int windowIndex) {
		return new ExpectedCondition<String>() {
			public String apply(final WebDriver from) {
				return getContextByIndex(from, windowIndex);
			}
		};
	}
	
	

}
