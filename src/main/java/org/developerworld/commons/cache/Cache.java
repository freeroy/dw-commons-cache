package org.developerworld.commons.cache;

import java.util.Set;

/**
 * 缓存处理对象
 * @author Roy Huang
 * @version 20111006
 *
 */
public interface Cache {

	/**
	 * 写入缓存
	 * @param key
	 * @param value
	 */
	public void put(String key,Object value);
	
	/**
	 * 获取缓存数据
	 * @param key
	 * @return
	 */
	public Object get(String key);
	
	/**
	 * 删除缓存
	 * @param key
	 */
	public void remove(String key);
	
	/**
	 * 删除所有缓存
	 */
	public void removeAll();
	
	/**
	 * 获取缓存量
	 * @return
	 */
	public int size();
	
	/**
	 * 获取所有key
	 * @return
	 */
	public Set<String> getKeys();

}
