package org.developerworld.commons.cache.impl;

import org.developerworld.commons.cache.CacheKeyGenerator;


/**
 * 默认缓存key生成器
 * @author Roy Huang
 * @version 20111010
 *
 */
public class StandardCacheKeyGenerator implements CacheKeyGenerator {

	public String generate(String cacheName, String[] cacheNodes, Object[] keyArgs) {
		StringBuilder rst = new StringBuilder(cacheName);
		if (cacheNodes != null && cacheNodes.length > 0) {
			for (String cacheNode : cacheNodes)
				rst.append("_").append(cacheNode);
		}
		// 根据参数，生成hashcode
		int argsHashCode = buildHashCode(keyArgs);
		// 返回完整的key
		return rst.append("_").append(argsHashCode).toString();
	}
	
	/**
	 * 获取参数hashcode
	 * 
	 * @param keyArgs
	 * @return
	 */
	private int buildHashCode(Object[] keyArgs) {
		if (keyArgs == null || keyArgs.length == 0)
			return 0;
		else if (keyArgs.length == 1)
			return (keyArgs[0] == null ? 53 : keyArgs[0].hashCode());
		else {
			int hashCode = 17;
			for (Object object : keyArgs) {
				hashCode = 31 * hashCode
						+ (object == null ? 53 : object.hashCode());
			}
			return hashCode;
		}
	}

}
