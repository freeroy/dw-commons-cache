package org.developerworld.commons.cache;

/**
 * 缓存管理器，负责派生对应的CacheFactory
 * @author Roy Huang
 * @version 20111006
 *
 */
public interface CacheManager {
	
	/**
	 * 初始化执行
	 */
	public void init();
	
	/**
	 * 销毁时执行
	 */
	public void destory();
	
	/**
	 * 根据缓存名，获取缓存对象
	 * @param cacheName
	 * @return
	 */
	public Cache getCache(String cacheName);

}
