package org.developerworld.commons.cache.aspectj;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.developerworld.commons.cache.CacheHandler;
import org.developerworld.commons.cache.NotInCacheMethod;
import org.developerworld.commons.cache.annotation.CacheEvict;
import org.developerworld.commons.cache.annotation.CacheEvicts;
import org.developerworld.commons.cache.annotation.Cacheable;
import org.developerworld.commons.cache.annotation.Cacheables;

/**
 * 缓存注解拦截器
 * 
 * @author Roy Huang
 * @version 20130624
 * 
 */
public class CacheAnnotationAspect {

	private static Log log = LogFactory.getLog(CacheAnnotationAspect.class);

	private CacheHandler cacheHandler;

	public void setCacheHandler(CacheHandler cacheHandler) {
		this.cacheHandler = cacheHandler;
	}

	public CacheAnnotationAspect() {
		log.info(getClass() + " new!");
	}

	/**
	 * 处理@Cacheable注解
	 */
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		Object rst = null;
		Method method = null;
		try {
			method = ((MethodSignature) pjp.getSignature()).getMethod();
			// 若方法存在缓存定义
			if (method.isAnnotationPresent(Cacheables.class)) {
				Cacheables cacheables = method.getAnnotation(Cacheables.class);
				rst = invokeCacheables(pjp, cacheables);
			} else if (method.isAnnotationPresent(Cacheable.class)) {
				Cacheable cacheable = method.getAnnotation(Cacheable.class);
				rst = invokeCacheable(pjp, cacheable);
			} else
				rst = pjp.proceed();
			return rst;
		} finally {
			// 若方法存在缓存定义
			if (method.isAnnotationPresent(CacheEvicts.class)) {
				CacheEvicts cacheEvicts = method
						.getAnnotation(CacheEvicts.class);
				for (CacheEvict cacheEvict : cacheEvicts.value())
					afterReturningCacheEvict(rst, method, pjp.getArgs(),
							pjp.getTarget(), cacheEvict);
			} else if (method.isAnnotationPresent(CacheEvict.class)) {
				CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
				afterReturningCacheEvict(rst, method, pjp.getArgs(),
						pjp.getTarget(), cacheEvict);
			}
		}
	}

	/**
	 * 根据cacheable配置执行方法
	 * 
	 * @param pjp
	 * @param cacheables
	 * @return
	 * @throws Throwable
	 */
	private Object invokeCacheables(ProceedingJoinPoint pjp,
			Cacheables cacheables) throws Throwable {
		Object rst = null;
		// 查找所有缓存，检查是否已经缓存数据
		for (Cacheable cacheable : cacheables.value()) {
			// 获取配置数据
			String cacheName = cacheable.cacheName();
			String cacheKey = cacheable.cacheKey();
			String[] cacheNodes = cacheable.cacheNodes();
			// 若cacheKey为空，则传入执行参数动态创建
			if (StringUtils.isEmpty(cacheKey))
				rst = cacheHandler.getFromCache(cacheName, cacheNodes,
						pjp.getArgs());
			// 否则直接采用指定的cacheKey
			else
				rst = cacheHandler
						.getFromCache(cacheName, cacheNodes, cacheKey);
			if (rst != null)
				break;
		}
		// 若其中一个缓存中存在数据，则把该数据同步到其他缓存中
		if (rst != null)
			putInCache(cacheables, pjp.getArgs(), rst);
		// 否则代表所有缓存都无数据，就执行方法，并写入缓存
		else {
			rst = pjp.proceed();
			putInCache(cacheables, pjp.getArgs(), rst);
		}
		return rst;
	}

	/**
	 * 把数据写入指定配置的缓存
	 * 
	 * @param cacheables
	 * @param args
	 * @param data
	 */
	private void putInCache(Cacheables cacheables, Object[] args, Object data) {
		for (Cacheable cacheable : cacheables.value()) {
			// 获取配置数据
			String cacheName = cacheable.cacheName();
			String cacheKey = cacheable.cacheKey();
			String[] cacheNodes = cacheable.cacheNodes();
			// 若cacheKey为空，则传入执行参数动态创建
			if (StringUtils.isEmpty(cacheKey))
				cacheHandler.putInCache(cacheName, cacheNodes, args, data);
			// 否则直接采用指定的cacheKey
			else
				cacheHandler.putInCache(cacheName, cacheNodes, cacheKey, data);
		}
	}

	/**
	 * 根据cacheable配置执行方法
	 * 
	 * @param pjp
	 * @param cacheable
	 * @return
	 * @throws Throwable
	 */
	private Object invokeCacheable(final ProceedingJoinPoint pjp,
			Cacheable cacheable) throws Throwable {
		Object rst = null;
		// 获取配置数据
		String cacheName = cacheable.cacheName();
		String cacheKey = cacheable.cacheKey();
		String[] cacheNodes = cacheable.cacheNodes();
		// 定义回调方法
		NotInCacheMethod<Object> callback = new NotInCacheMethod<Object>() {
			public Object invoke() throws Throwable {
				return pjp.proceed();
			}
		};
		// 若cacheKey为空，则传入执行参数动态创建
		if (StringUtils.isEmpty(cacheKey))
			rst = cacheHandler.getFromCache(cacheName, cacheNodes,
					pjp.getArgs(), callback);
		// 否则直接采用指定的cacheKey
		else
			rst = cacheHandler.getFromCache(cacheName, cacheNodes, cacheKey,
					callback);
		return rst;
	}

	/**
	 * 根据CacheEvict执行操作
	 * 
	 * @param cacheEvict
	 */
	private void afterReturningCacheEvict(Object rst, Method method,
			Object[] args, Object target, CacheEvict cacheEvict) {
		try {
			String cacheName = cacheEvict.cacheName();
			String[] cacheNodes = cacheEvict.cacheNodes();
			String cacheKey = cacheEvict.cacheKey();
			boolean allCache = cacheEvict.allCache();
			boolean cascadeCacheNode = cacheEvict.cascadeCacheNode();
			boolean allCacheNodeCache = cacheEvict.allCacheNodeCache();
			// 清空指定缓存所有数据
			if (allCache)
				cacheHandler.removeCache(cacheName);
			else {
				// 清空节点所有缓存
				if (allCacheNodeCache)
					cacheHandler.removeCache(cacheName, cacheNodes,
							cascadeCacheNode);
				// 清空节点对应cacheKey缓存
				else if (StringUtils.isEmpty(cacheKey))
					cacheHandler.removeCache(cacheName, cacheNodes, args,
							cascadeCacheNode);
				else
					cacheHandler.removeCache(cacheName, cacheNodes, cacheKey,
							cascadeCacheNode);
			}
		} catch (Throwable t) {
			log.error(t);
		}
	}
}
