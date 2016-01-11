package org.developerworld.commons.cache.impl.ehcache;

import java.io.InputStream;
import java.net.URL;

import org.developerworld.commons.cache.Cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;

/**
 * EHCache管理类
 * 
 * @author Roy Huang
 *
 */
public class EHCacheManager implements org.developerworld.commons.cache.CacheManager {

	private String configPath;
	private URL configUrl;
	private Configuration configuration;
	private InputStream configInputStream;
	private CacheManager cacheManager;
	private net.sf.ehcache.Cache defauleCache;

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setConfigInputStream(InputStream configInputStream) {
		this.configInputStream = configInputStream;
	}

	public void setConfigUrl(URL configUrl) {
		this.configUrl = configUrl;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setDefauleCache(net.sf.ehcache.Cache defauleCache) {
		this.defauleCache = defauleCache;
	}

	public void init() {
		if (cacheManager == null) {
			if (configuration != null)
				cacheManager = CacheManager.newInstance(configuration);
			else if (configInputStream != null)
				cacheManager = CacheManager.newInstance(configInputStream);
			else if (configUrl != null)
				cacheManager = CacheManager.newInstance(configUrl);
			else if (configPath != null)
				cacheManager = CacheManager.newInstance(configPath);
			else
				cacheManager = CacheManager.newInstance();
		}
	}

	public void destory() {
		cacheManager.shutdown();
		cacheManager = null;
	}

	public Cache getCache() {
		if(defauleCache==null)
			throw new RuntimeException("the defauleCache is not found!");
		return new EHCache(defauleCache);
	}

	public Cache getCache(String cacheName) {
		return new EHCache(cacheManager.getCache(cacheName));
	}
}
