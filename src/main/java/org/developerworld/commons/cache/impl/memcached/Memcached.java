package org.developerworld.commons.cache.impl.memcached;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.danga.MemCached.MemCachedClient;

/**
 * 针对memcached的缓存实现
 * 
 * @author Roy Huang
 *
 */
public class Memcached implements org.developerworld.commons.cache.Cache {

	private MemCachedClient memCachedClient;
	private String keyPrefix;
	private Long cacheExpiryTime;

	public Memcached(MemCachedClient memCachedClient) {
		this(null, memCachedClient);
	}

	public Memcached(String cacheName, MemCachedClient memCachedClient) {
		this(cacheName, memCachedClient, null);
	}

	public Memcached(String cacheName, MemCachedClient memCachedClient, Long cacheExpiryTime) {
		this.memCachedClient = memCachedClient;
		this.keyPrefix = cacheName;
		this.cacheExpiryTime = cacheExpiryTime;
	}

	public MemCachedClient getCache() {
		return memCachedClient;
	}

	/**
	 * 构建key
	 * 
	 * @param key
	 * @return
	 */
	private String buildKey(String key) {
		return getKeyPrefix() + key;
	}

	/**
	 * 获取key前缀
	 * 
	 * @return
	 */
	private String getKeyPrefix() {
		return keyPrefix == null ? "" : keyPrefix + "_";
	}

	public void put(String key, Object value) {
		if (cacheExpiryTime != null)
			put(key, value, cacheExpiryTime);
		else if (!memCachedClient.add(buildKey(key), value))
			throw new RuntimeException("can not put the data to cache");
	}

	public void put(String key, Object value, long cacheExpiryTime) {
		if (!memCachedClient.add(buildKey(key), value, new Date(System.currentTimeMillis() + cacheExpiryTime)))
			throw new RuntimeException("can not put the data to cache");
	}

	public Object get(String key) {
		return memCachedClient.get(buildKey(key));
	}

	public void remove(String key) {
		if (!memCachedClient.delete(buildKey(key)))
			throw new RuntimeException("can not remove the data on cache");
	}

	public void removeAll() {
		if (!memCachedClient.flushAll())
			throw new RuntimeException("can not remove datas on cache");
	}

	public int size() {
		int rst = 0;
		if (StringUtils.isBlank(getKeyPrefix())) {
			// 获取集群统计信息
			Map<String, Map<String, String>> statsItems = memCachedClient.statsItems();
			if (statsItems != null) {
				Set<Entry<String, Map<String, String>>> statsItemsSet = statsItems.entrySet();
				for (Entry<String, Map<String, String>> statsItemsEntry : statsItemsSet) {
					Map<String, String> statsItem = statsItemsEntry.getValue();
					// 获取单一节点统计信息
					Set<Entry<String, String>> statsItemSet = statsItem.entrySet();
					for (Entry<String, String> statsItemEntry : statsItemSet) {
						// 若是描述节点量，最后一位字符串为number（items:2:number=2）
						if (statsItemEntry.getKey().endsWith("number")) {
							// 该slab下的元素个数
							int limit = Integer.valueOf(statsItemEntry.getValue().trim());
							rst += limit;
						}
					}
				}
			}
		} else
			rst = getKeys().size();
		return rst;
	}

	public Set<String> getKeys() {
		Set<String> rst = new HashSet<String>();
		// 获取集群统计信息
		Map<String, Map<String, String>> statsItems = memCachedClient.statsItems();
		if (statsItems != null) {
			Set<Entry<String, Map<String, String>>> statsItemsSet = statsItems.entrySet();
			for (Entry<String, Map<String, String>> statsItemsEntry : statsItemsSet) {
				Map<String, String> statsItem = statsItemsEntry.getValue();
				// 获取单一节点统计信息
				Set<Entry<String, String>> statsItemSet = statsItem.entrySet();
				for (Entry<String, String> statsItemEntry : statsItemSet) {
					// 若是描述节点量，最后一位字符串为number（items:2:number=2）
					if (statsItemEntry.getKey().endsWith("number")) {
						String[] tmpArray = statsItemEntry.getKey().split(":");
						// 获取slab id
						int slabNumber = Integer.valueOf(tmpArray[1].trim());
						// 该slab下的元素个数
						int limit = Integer.valueOf(statsItemEntry.getValue().trim());
						// 获取slab的key信息
						Map<String, Map<String, String>> statsCacheDump = memCachedClient.statsCacheDump(slabNumber,
								limit);
						if (statsCacheDump != null) {
							Set<Entry<String, Map<String, String>>> statsCacheDumpsSet = statsCacheDump.entrySet();
							for (Entry<String, Map<String, String>> statsCacheDumpsEntry : statsCacheDumpsSet) {
								Map<String, String> statsCacheDumpEntry = statsCacheDumpsEntry.getValue();
								if (statsCacheDumpEntry != null) {
									Set<String> keys = statsCacheDumpEntry.keySet();
									for (String key : keys)
										if (key.trim().startsWith(getKeyPrefix()))
											rst.add(key.trim());
								}
							}
						}
					}
				}
			}
		}
		return rst;
	}

}
