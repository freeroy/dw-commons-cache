package org.developerworld.commons.cache.impl.ehcache;

import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * Ehcache内存对象
 * 
 * @author Roy Huang
 * 
 */
public class EHCache implements org.developerworld.commons.cache.Cache {

	Cache cache;

	public EHCache(Cache cache) {
		this.cache = cache;
	}

	public Cache getCache() {
		return cache;
	}

	public void put(String key, Object value) {
		cache.put(new Element(key, value));
	}

	public Object get(String key) {
		Element e = cache.get(key);
		return e == null ? null : e.getObjectValue();
	}

	public void remove(String key) {
		cache.remove(key);
	}

	public int size() {
		return cache.getSize();
	}

	public Set<String> getKeys() {
		return new LinkedHashSet<String>(cache.getKeys());
	}

	public void removeAll() {
		cache.removeAll();
	}

}
