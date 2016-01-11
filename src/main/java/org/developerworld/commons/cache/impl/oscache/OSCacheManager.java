package org.developerworld.commons.cache.impl.oscache;

import java.util.Properties;

import org.developerworld.commons.cache.Cache;
import org.developerworld.commons.cache.CacheManager;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * OSCache 管理类
 * 
 * @author Roy Huang
 *
 */
public class OSCacheManager implements CacheManager {

	private GeneralCacheAdministrator admin;
	private Properties properties;

	public void setConfigProperties(Properties properties) {
		this.properties = properties;
	}

	public void init() {
		if (properties != null)
			admin = new GeneralCacheAdministrator(properties);
		else
			admin = new GeneralCacheAdministrator();
	}

	public void destory() {
		admin.destroy();
		admin = null;
	}

	public Cache getCache() {
		return new OSCache(admin.getCache());
	}

	public Cache getCache(String cacheName) {
		return new OSCache(cacheName, admin.getCache());
	}

}
