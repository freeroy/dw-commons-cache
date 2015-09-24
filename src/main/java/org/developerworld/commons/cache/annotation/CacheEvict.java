package org.developerworld.commons.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存销毁注解
 * 
 * @author Roy Huang
 * @version 20111010
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {

	/**
	 * 缓存的名称
	 * 
	 * @return
	 */
	String cacheName();

	/**
	 * 缓存节点 (为空或长度为0，则表示保存在默认节点key=null)
	 * 
	 * @return
	 */
	String[] cacheNodes() default {};

	/**
	 * 缓存key的名字 (默认为空，则表示根据cacheKeyGenerator创建)
	 * 
	 * @return
	 */
	String cacheKey() default "";
	
	/**
	 * 是否清空指定节点下所有缓存(默认为true,则忽略cacheKey,清空指定节点的所有缓存;false,根据cacheKey进行清空)
	 * @return
	 */
	boolean allCacheNodeCache() default true;

	/**
	 * 是否删除指定cacheName的所有缓存 (默认为false，则不删除指定cacheName中的所有缓存)
	 * 
	 * @return
	 */
	boolean allCache() default false;

	/**
	 * 清理操作是否级联子级节点执行 (默认为true，同时执行子节点的相同操作；false，子节点不响应操作)
	 * 
	 * @return
	 */
	boolean cascadeCacheNode() default true;
}
