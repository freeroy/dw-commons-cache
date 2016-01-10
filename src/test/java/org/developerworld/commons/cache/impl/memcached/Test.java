package org.developerworld.commons.cache.impl.memcached;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class Test {
	
	private static MemcachedManager cacheManager;
	
	@BeforeClass
	public static void beforeClass(){
		cacheManager=new MemcachedManager();
		cacheManager.setServers(new String[]{"127.0.0.1:11211"});
		cacheManager.init();
	}
	
	@AfterClass
	public static void afterClass(){
		cacheManager.destory();
	}

	@org.junit.Test
	public void test(){
		Memcached cache=(Memcached) cacheManager.getCache(null);
		System.out.println(cache.get("data"));
		System.out.println(cache.get("data2"));
		String data="i set it :"+new Date();
		String data2="i set it 2:"+new Date();
		cache.put("data", data);
		cache.put("data2", data2);
		System.out.println(cache.get("data"));
		System.out.println(cache.get("data2"));
		System.out.println("key:"+cache.getKeys());
		System.out.println("cache site:"+cache.size());
	}
}
