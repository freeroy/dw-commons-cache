package org.developerworld.commons.cache;

/**
 * 当缓存数据不存在的时候执行的方法接口
 * @author Roy Huang
 * @version 20111010
 *
 * @param <T>
 */
public interface NotInCacheMethod<T> {

	/**
	 * 回调执行方法
	 * @return
	 * @throws Throwable 
	 */
	public T invoke() throws Throwable;
	
}
