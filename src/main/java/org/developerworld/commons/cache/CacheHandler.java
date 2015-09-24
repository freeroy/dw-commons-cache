package org.developerworld.commons.cache;


/**
 * 缓存控制器接口
 * 
 * @author Roy Huang
 * @version 20111010
 * 
 */
public interface CacheHandler {

	/**
	 * 把数据写入缓存
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheKey
	 * @param data
	 */
	public <T> void putInCache(String cacheName, String cacheKey, T data);

	/**
	 * 把数据写入缓存
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param keyArgs
	 * @param data
	 */
	public <T> void putInCache(String cacheName, Object[] keyArgs, T data);

	/**
	 * 把数据写入缓存
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheNodes
	 * @param cacheKey
	 * @param data
	 */
	public <T> void putInCache(String cacheName, String[] cacheNodes,
			String cacheKey, T data);

	/**
	 * 把数据写入缓存
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheNodes
	 * @param keyArgs
	 * @param data
	 */
	public <T> void putInCache(String cacheName, String[] cacheNodes,
			Object[] keyArgs, T data);

	/**
	 * 从缓存获取数据
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheKey
	 * @return
	 */
	public <T> T getFromCache(String cacheName, String cacheKey);

	/**
	 * 从缓存获取数据
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param keyArgs
	 * @return
	 */
	public <T> T getFromCache(String cacheName, Object[] keyArgs);

	/**
	 * 从缓存获取数据
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheNodes
	 * @param keyArgs
	 * @return
	 */
	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			Object[] keyArgs);

	/**
	 * 从缓存获取数据
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheNodes
	 * @param cacheKey
	 * @return
	 */
	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			String cacheKey);

	/**
	 * 从缓存获取数据
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheNodes
	 * @param keyArgs
	 * @param notInCacheMethod
	 * @return
	 * @throws Throwable
	 */
	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			Object[] keyArgs, NotInCacheMethod<T> notInCacheMethod)
			throws Throwable;

	/**
	 * 从缓存获取数据
	 * 
	 * @param <T>
	 * @param cacheName
	 * @param cacheNodes
	 * @param cacheKey
	 * @param notInCacheMethod
	 * @return
	 * @throws Throwable
	 */
	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			String cacheKey, NotInCacheMethod<T> notInCacheMethod)
			throws Throwable;

	/**
	 * 删除缓存
	 * 
	 * @param cacheName
	 */
	public void removeCache(String cacheName);

	/**
	 * 删除缓存
	 * 
	 * @param cacheName
	 * @param cacheNodes
	 * @param cascadeCacheNode
	 */
	public void removeCache(String cacheName, String[] cacheNodes,
			boolean cascadeCacheNode);

	/**
	 * 删除缓存
	 * 
	 * @param cacheName
	 * @param cacheNodes
	 * @param args
	 * @param cascadeCacheNode
	 */
	public void removeCache(String cacheName, String[] cacheNodes,
			Object[] args, boolean cascadeCacheNode);

	/**
	 * 删除缓存
	 * 
	 * @param cacheName
	 * @param cacheNodes
	 * @param cacheKey
	 * @param cascadeCacheNode
	 */
	public void removeCache(String cacheName, String[] cacheNodes,
			String cacheKey, boolean cascadeCacheNode);

}