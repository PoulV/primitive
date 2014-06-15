package org.primitive.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.primitive.model.abstraction.ModelObject;
import org.primitive.model.interfaces.IDecomposable;
import org.primitive.model.interfaces.IHasManyHandles;
import org.primitive.webdriverencapsulations.Handle;
import org.primitive.webdriverencapsulations.Manager;
import org.primitive.webdriverencapsulations.WebDriverEncapsulation;
import org.primitive.webdriverencapsulations.components.overriden.Cookies;
import org.primitive.webdriverencapsulations.components.overriden.TimeOut;

/**
 * Common representation of any application 
 * Using it you can model your testing application as a complex aggregated
 * object it should only generate new objects in general
 */
abstract class Application extends ModelObject implements IHasManyHandles {

	protected final Cookies cookies;
	protected final Manager manager;
	protected final TimeOut timeOuts;

	public Application(Handle handle) {
		super(handle);
		cookies = driverEncapsulation.getCookies();
		timeOuts = driverEncapsulation.getTimeOut();
		manager = handle.getManager();
	}	
	
	static Class<?>[] replaceHandleParamIfItNeedsToBe(Class<?>[] originalParams,
			Class<?> required, Handle actualHandle) {
		Class<?>[] result = null;
		try{
			required.getDeclaredConstructor(originalParams);
			return originalParams;
		}
		catch (Exception ignored){}
		
		ArrayList<Class<?>> params = new ArrayList<>(Arrays.asList(originalParams)); 
		int i = params.indexOf(Handle.class);
		params.remove(Handle.class);
		params.add(i, actualHandle.getClass());
		result = params.toArray(new Class<?>[] {});
		try{
			required.getDeclaredConstructor(result);
		}
		catch (Exception e){
			throw new RuntimeException(e);
		}
		return result;		
	}
	
	/**
	 * using any accessible (!!!) FunctionalPart constructor Application creates page
	 * objects.
	 */
	protected <T extends IDecomposable> T get(Class<T> partClass,
			Class<?>[] params, Object[] values) {
		T part = DefaultApplicationFactory.get(partClass, params, values);
				//get(partClass, params, values);
		((FunctionalPart) part).application = this;
		addChild((ModelObject) part);
		return part;
	}

	/**
	 * Gets a functional part (page object) from the new opened handle 
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass) {
		Handle newHandle = manager.getNewHandle();
		Class<?>[] params = new Class[] {Handle.class};
		Object[] values = new Object[] {newHandle};
		return get(partClass, replaceHandleParamIfItNeedsToBe(params, partClass, newHandle), 
				values);
	}
	
	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified timeout 
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass,
			long timeOutSec) {
		Handle newHandle = manager.getNewHandle(timeOutSec);
		Class<?>[] params = new Class[] {Handle.class};
		Object[] values = new Object[] {newHandle};
		return get(partClass, replaceHandleParamIfItNeedsToBe(params, partClass, newHandle), 
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified string identification 
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass,
			String stringIdentifier) {
		Handle newHandle = manager.getNewHandle(stringIdentifier);
		Class<?>[] params = new Class[] {Handle.class};
		Object[] values = new Object[] {newHandle};
		return get(partClass, replaceHandleParamIfItNeedsToBe(params, partClass, newHandle), 
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified string identification and timeout
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass,
			String stringIdentifier, long timeOutSec) {
		Handle newHandle = manager.getNewHandle(timeOutSec, stringIdentifier);
		Class<?>[] params = new Class[] {Handle.class};
		Object[] values = new Object[] {newHandle};
		return get(partClass, replaceHandleParamIfItNeedsToBe(params, partClass, newHandle), 
				values);
	}

	/**
	 * Gets a functional part (page object) from the existing handle by index
	 */
	@Override
	public <T extends IDecomposable> T getFromHandle(Class<T> partClass,
			int index) {
		Handle newHandle = manager.getByInex(index);
		Class<?>[] params = new Class[] { Handle.class };
		Object[] values = new Object[] { newHandle };
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the main application 
	 * handle.
	 */
	@Override
	public <T extends IDecomposable> T getPart(Class<T> partClass) {
		Class<?>[] params = new Class[] {Handle.class};
		Object[] values = new Object[] {handle};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, handle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the main application 
	 * handle using indexed frame
	 */
	@Override
	public <T extends IDecomposable> T getPart(Class<T> partClass,
			Integer frameIndex) {
		Class<?>[] params = new Class[] {Handle.class, Integer.class};
		Object[] values = new Object[] {handle, frameIndex};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, handle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the main application 
	 * handle using path to frame
	 */
	@Override
	public <T extends IDecomposable> T getPart(Class<T> partClass,
			String pathToFrame) {
		Class<?>[] params = new Class[] {Handle.class, String.class};
		Object[] values = new Object[] {handle, pathToFrame};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, handle),
				values);
	}
	
	/**
	 * Gets a functional part (page object) from the existing handle by index
	 * and frame index
	 */
	@Override
	public <T extends IDecomposable> T getFromHandle(Class<T> partClass,
			Integer frameIndex, int index) {
		Handle newHandle = manager.getByInex(index);
		Class<?>[] params = new Class[] {Handle.class, Integer.class};
		Object[] values = new Object[] {newHandle, frameIndex};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the existing handle by index
	 * and path to frame 
	 */
	@Override
	public <T extends IDecomposable> T getFromHandle(Class<T> partClass,
			String pathToFrame, int index) {
		Handle newHandle = manager.getByInex(index);
		Class<?>[] params = new Class[] {Handle.class, String.class};
		Object[] values = new Object[] {newHandle, pathToFrame};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}
	
	/**
	 *  destroys an Application instance and makes WebDriver quit
	 */
	public void quit() {
		destroy();
		manager.destroy();
		driverEncapsulation.destroy();
	}

	/**
	 * Gets a functional part (page object) from the new opened handle 
	 * using frame index
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass,
			Integer frameIndex) {
		Handle newHandle = manager.getNewHandle();
		Class<?>[] params = new Class[] {Handle.class, Integer.class};
		Object[] values = new Object[] {newHandle, frameIndex};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle 
	 * using path to frame
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(String pathToFrame,
			Class<T> partClass) {
		Handle newHandle = manager.getNewHandle();
		Class<?>[] params = new Class[] {Handle.class, String.class};
		Object[] values = new Object[] {newHandle, pathToFrame};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified timeout and frame index
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass,
			long timeOutSec, Integer frameIndex) {
		Handle newHandle = manager.getNewHandle(timeOutSec);
		Class<?>[] params = new Class[] {Handle.class, Integer.class};
		Object[] values = new Object[] {newHandle, frameIndex};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified timeout and path to frame
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(String pathToFrame,
			Class<T> partClass, long timeOutSec) {
		Handle newHandle = manager.getNewHandle(timeOutSec);
		Class<?>[] params = new Class[] {Handle.class, String.class};
		Object[] values = new Object[] {newHandle, pathToFrame};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified string identification, timeout and index of required frame
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass,
			String stringIdentifier, long timeOutSec, Integer frameIndex) {
		Handle newHandle = manager.getNewHandle(timeOutSec, stringIdentifier);
		Class<?>[] params = new Class[] {Handle.class, Integer.class};
		Object[] values = new Object[] {newHandle, frameIndex};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified string identification, timeout and path to required frame
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(String pathToFrame,
			Class<T> partClass, String stringIdentifier, long timeOutSec) {
		Handle newHandle = manager.getNewHandle(timeOutSec, stringIdentifier);
		Class<?>[] params = new Class[] {Handle.class, String.class};
		Object[] values = new Object[] {newHandle, pathToFrame};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified string identification and specified frame index
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(Class<T> partClass,
			String stringIdentifier, Integer frameIndex) {
		Handle newHandle = manager.getNewHandle(stringIdentifier);
		Class<?>[] params = new Class[] {Handle.class, Integer.class};
		Object[] values = new Object[] {newHandle, frameIndex};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}

	/**
	 * Gets a functional part (page object) from the new opened handle using 
	 * specified string identification and specified path to frame
	 */
	@Override
	public <T extends IDecomposable> T getFromNewHandle(String pathToFrame,
			Class<T> partClass, String stringIdentifier) {
		Handle newHandle = manager.getNewHandle(stringIdentifier);
		Class<?>[] params = new Class[] {Handle.class, String.class};
		Object[] values = new Object[] {newHandle, pathToFrame};
		return get(partClass,
				replaceHandleParamIfItNeedsToBe(params, partClass, newHandle),
				values);
	}
	
	WebDriverEncapsulation getWebDriverEncapsulation(){
		return driverEncapsulation;
	}
}
