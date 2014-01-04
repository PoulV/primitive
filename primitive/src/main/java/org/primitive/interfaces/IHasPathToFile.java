package org.primitive.interfaces;

/**
 * @author s.tihomirov
 *for all that can return a path to some file
 */
public interface IHasPathToFile extends IHasPathToFolder {
	public String getFile();
}
