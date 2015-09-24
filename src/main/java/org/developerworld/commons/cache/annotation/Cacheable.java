package org.developerworld.commons.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解
 * 
 * @author Roy Huang
 * @version 20111009
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {

	/**
	 * 缓存的名称
	 * 
	 * @return
	 */
	String cacheName();

	/**
	 * 缓存节点(为空或长度为0，则表示保存在默认节点key=null)
	 * 
	 * @return
	 */
	String[] cacheNodes() default {};

	/**
	 * 缓存key的名字(默认为空，则表示根据cacheKeyGenerator创建)
	 * 
	 * @return
	 */
	String cacheKey() default "";
}
