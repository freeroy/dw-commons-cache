package org.developerworld.commons.cache.impl.oscache;

import java.util.LinkedHashSet;
import java.util.Set;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * OSCache缓存类
 * 
 * @author Roy Huang
 * 
 */
public class OSCache implements org.developerworld.commons.cache.Cache {

	private Cache cache;
	private String keyPrefix;

	public OSCache(Cache cache) {
		this(null, cache);
	}

	public OSCache(String keyPrefix, Cache cache) {
		this.keyPrefix = keyPrefix;
		this.cache = cache;
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

	/**
	 * 返回内部缓存对象
	 * 
	 * @return
	 */
	public Cache getCache() {
		return cache;
	}

	public void put(String key, Object value) {
		cache.putInCache(buildKey(key), value);
		updateKeys(key, false);
	}

	public Object get(String key) {
		Object rst = null;
		try {
			rst = cache.getFromCache(buildKey(key));
		} catch (NeedsRefreshException e) {
			e.printStackTrace();
		}
		return rst;
	}

	public void remove(String key) {
		cache.removeEntry(buildKey(key));
		updateKeys(key, true);
	}

	/**
	 * 更新key
	 * 
	 * @param key
	 */
	private void updateKeys(String key, boolean isRemove) {
		String keysKey = getKeysKey();
		Set<String> keys = null;
		try {
			keys = (Set<String>) cache.getFromCache(keysKey);
		} catch (NeedsRefreshException e) {
			e.printStackTrace();
		}
		if (keys == null)
			keys = new LinkedHashSet<String>();
		if (isRemove && keys.contains(key))
			keys.remove(key);
		else if (!keys.contains(key))
			keys.add(key);
		cache.putInCache(keysKey, keys);
	}

	/**
	 * 获取keys缓存的key
	 * 
	 * @return
	 */
	private String getKeysKey() {
		return this.getClass().getName() + "_" + getKeyPrefix() + "keys";
	}

	public void removeAll() {
		Set<String> keys = getKeys();
		for (String key : keys)
			remove(key);
	}

	public int size() {
		return getKeys().size();
	}

	public Set<String> getKeys() {
		Set<String> rst = null;
		try {
			String keysKey = getKeysKey();
			rst = (Set<String>) cache.getFromCache(keysKey);
			if (rst == null)
				rst = new LinkedHashSet<String>();
		} catch (NeedsRefreshException e) {
			e.printStackTrace();
		}
		return rst;
	}
}
