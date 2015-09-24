package org.developerworld.commons.cache.handler.simple;

import org.developerworld.commons.cache.impl.StandardCacheHandler;
import org.developerworld.commons.cache.impl.StandardCacheKeyGenerator;
import org.developerworld.commons.cache.impl.ehcache.EHCacheManager;
import org.junit.Test;

public class SimpleCacheHandlerTest {

	@Test
	public void test(){
		EHCacheManager cacheManager=new EHCacheManager();
		cacheManager.setConfigInputStream(this.getClass().getResourceAsStream("ehcache.xml"));
		cacheManager.init();
		try{
			StandardCacheHandler cacheHandler=new StandardCacheHandler();
			cacheHandler.setCacheManager(cacheManager);
			cacheHandler.setCacheKeyGenerator(new StandardCacheKeyGenerator());
			
			System.out.println(cacheHandler.getFromCache("mobileSmsLog", "test"));
			cacheHandler.putInCache("mobileSmsLog", "test", 1);
			System.out.println(cacheHandler.getFromCache("mobileSmsLog", "test"));
		}
		finally{
			cacheManager.destory();
		}
		
	}
}
