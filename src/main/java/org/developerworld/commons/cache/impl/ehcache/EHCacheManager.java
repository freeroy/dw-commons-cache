package org.developerworld.commons.cache.impl.ehcache;

import java.io.InputStream;
import java.net.URL;

import org.developerworld.commons.cache.Cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;

/**
 * EHCache管理类
 * @author Roy Huang
 * @version 20111006
 *
 */
public class EHCacheManager implements
		org.developerworld.commons.cache.CacheManager {

	private String configPath;
	private URL configUrl;
	private CacheManager cacheManager;
	private Configuration configuration;
	private InputStream configInputStream;

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
		cacheManager=null;
	}

	public Cache getCache(String cacheName) {
		return new EHCache(cacheManager.getCache(cacheName));
	}

}
