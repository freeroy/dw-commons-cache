package org.developerworld.commons.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.developerworld.commons.cache.Cache;
import org.developerworld.commons.cache.CacheHandler;
import org.developerworld.commons.cache.CacheKeyGenerator;
import org.developerworld.commons.cache.CacheManager;
import org.developerworld.commons.cache.NotInCacheMethod;

/**
 * 缓存控制器
 * 
 * @author Roy Huang
 * @version 20111010
 * 
 */
public class StandardCacheHandler implements CacheHandler {

	private static Log log = LogFactory.getLog(CacheManager.class);

	private CacheManager cacheManager;

	private CacheKeyGenerator cacheKeyGenerator;

	private Map<String, Map<String, CacheNode>> cacheTree = new HashMap<String, Map<String, CacheNode>>();

	public void setCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
		this.cacheKeyGenerator = cacheKeyGenerator;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public <T> void putInCache(String cacheName, Object[] keyArgs, T data) {
		putInCache(cacheName, null, keyArgs, data);
	}

	public <T> void putInCache(String cacheName, String cacheKey, T data) {
		putInCache(cacheName, null, cacheKey, data);
	}

	public <T> void putInCache(String cacheName, String[] cacheNodes,
			Object[] keyArgs, T data) {
		putInCache(cacheName, null,
				generatorCacheKey(cacheName, cacheNodes, keyArgs), data);
	}

	public <T> void putInCache(String cacheName, String[] cacheNodes,
			String cacheKey, T data) {
		try {
			// 把数据写入缓存
			getCache(cacheName).put(cacheKey, data);
			// 更新缓存记录树
			updateCacheNode(cacheName, cacheNodes, cacheKey);
		} catch (Throwable t) {
			log.error(t);
		}
	}

	public <T> T getFromCache(String cacheName, String cacheKey) {
		return (T)getFromCache(cacheName, null, cacheKey);
	}

	public <T> T getFromCache(String cacheName, Object[] keyArgs) {
		return (T)getFromCache(cacheName, null, keyArgs);
	}

	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			Object[] keyArgs) {
		T rst = null;
		try {
			rst = (T)getFromCache(cacheName, cacheNodes, keyArgs, null);
		} catch (Throwable e) {
			log.error(e);
		}
		return rst;
	}

	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			String cacheKey) {
		T rst = null;
		try {
			rst = (T)getFromCache(cacheName, cacheNodes, cacheKey, null);
		} catch (Throwable e) {
			log.error(e);
		}
		return rst;
	}

	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			Object[] keyArgs, NotInCacheMethod<T> notInCacheMethod)
			throws Throwable {
		// 获取对应的key
		String cacheKey = generatorCacheKey(cacheName, cacheNodes, keyArgs);
		return getFromCache(cacheName, cacheNodes, cacheKey, notInCacheMethod);
	}

	public <T> T getFromCache(String cacheName, String[] cacheNodes,
			String cacheKey, NotInCacheMethod<T> notInCacheMethod)
			throws Throwable {
		T rst = null;
		// 根据key获取数据
		try {
			rst = (T) getCache(cacheName).get(cacheKey);
		} catch (Throwable t) {
			log.error(t);
		}
		// 若数据不存在，则执行对应方法获取
		if (rst == null && notInCacheMethod != null) {
			rst = notInCacheMethod.invoke();
			// 把数据写入缓存
			putInCache(cacheName, cacheNodes, cacheKey, rst);
		}
		return rst;
	}

	/**
	 * 根据缓存名称获取缓存对象
	 * 
	 * @param cacheName
	 * @return
	 */
	private Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	/**
	 * 更新缓存类型记录数
	 * 
	 * @param cacheNodes
	 * @param cacheKey
	 */
	private void updateCacheNode(String cacheName, String[] cacheNodes,
			String cacheKey) {
		// 获取缓存树
		Map<String, CacheNode> tree = getCacheNodeTree(cacheName);
		// 获取缓存节点树
		String rootCacheNodeId = null;
		if (cacheNodes != null && cacheNodes.length > 0)
			rootCacheNodeId = cacheNodes[0];
		// 获取缓存树根节点
		if (!tree.containsKey(rootCacheNodeId))
			tree.put(rootCacheNodeId, new CacheNode(rootCacheNodeId));
		CacheNode root = tree.get(rootCacheNodeId);
		// 更新节点级联关系
		if (cacheNodes != null && cacheNodes.length > 1)
			updateCacheTypeTree(root, (String[]) ArrayUtils.subarray(
					cacheNodes, 1, cacheNodes.length), cacheKey);
		else
			root.getCacheKeys().add(cacheKey);
	}

	/**
	 * 更新节点级联关系
	 * 
	 * @param parent
	 * @param cacheKeys
	 */
	private void updateCacheTypeTree(CacheNode parent, String[] cacheKeys,
			String cacheKey) {
		String nodeId = parent.getId() + "_" + cacheKeys[0];
		// 检查是否已经有指定子节点,无就创建
		CacheNode node = parent.getCacheNodes().get(nodeId);
		if (node == null) {
			node = new CacheNode(nodeId);
			parent.getCacheNodes().put(nodeId, node);
		}
		if (cacheKeys.length > 1)
			updateCacheTypeTree(node, (String[]) ArrayUtils.subarray(cacheKeys,
					1, cacheKeys.length), cacheKey);
		else
			node.getCacheKeys().add(cacheKey);
	}

	/**
	 * 获取根缓存节点
	 * 
	 * @param cacheName
	 * @return
	 */
	private Map<String, CacheNode> getCacheNodeTree(String cacheName) {
		if (!cacheTree.containsKey(cacheName))
			cacheTree.put(cacheName, new HashMap<String, CacheNode>());
		return cacheTree.get(cacheName);
	}

	public void removeCache(String cacheName) {
		try {
			// 获取指定的树根
			Map<String, CacheNode> tree = getCacheNodeTree(cacheName);
			synchronized (tree) {
				// 遍历所有节点，执行级联清理
				for (CacheNode cacheNode : tree.values()) {
					if (cacheNode != null)
						removeAllCache(cacheName, cacheNode, true);
				}
				tree.clear();
			}
		} catch (Throwable t) {
			log.error(t);
		}
	}

	public void removeCache(String cacheName, String[] cacheNodes,
			boolean cascadeCacheNode) {
		try {
			CacheNode node = searchCacheTypeNode(cacheName, cacheNodes);
			if (node != null)
				removeAllCache(cacheName, node, cascadeCacheNode);
		} catch (Throwable t) {
			log.error(t);
		}
	}

	public void removeCache(String cacheName, String[] cacheNodes,
			Object[] args, boolean cascadeCacheNode) {
		removeCache(cacheName, cacheNodes,
				generatorCacheKey(cacheName, cacheNodes, args),
				cascadeCacheNode);
	}

	public void removeCache(String cacheName, String[] cacheNodes,
			String cacheKey, boolean cascadeCacheNode) {
		try {
			CacheNode node = searchCacheTypeNode(cacheName, cacheNodes);
			if (node != null)
				removeCache(cacheName, node, cacheKey, cascadeCacheNode);
		} catch (Throwable t) {
			log.error(t);
		}
	}

	/**
	 * 查找对应的节点
	 * 
	 * @param cacheNodes
	 * @return
	 */
	private CacheNode searchCacheTypeNode(String cacheName, String[] cacheNodes) {
		// 获取缓存树
		Map<String, CacheNode> tree = getCacheNodeTree(cacheName);
		String rootCacheNodeId = null;
		if (cacheNodes != null && cacheNodes.length > 0)
			rootCacheNodeId = cacheNodes[0];
		CacheNode cacheNode = tree.get(rootCacheNodeId);
		// 存在节点、是根节点、只要求返回一个节点
		if (cacheNode == null || cacheNodes == null || cacheNodes.length <= 1)
			return cacheNode;
		// 还需要找别的节点
		else
			return searchCacheTypeNode(cacheNode,
					(String[]) ArrayUtils.subarray(cacheNodes, 1,
							cacheNodes.length));
	}

	/**
	 * 查找对应类的节点
	 * 
	 * @param cacheTypeNode
	 * @param cacheKeys
	 * @return
	 */
	private CacheNode searchCacheTypeNode(CacheNode cacheTypeNode,
			String[] cacheKeys) {
		String nodeId = cacheTypeNode.getId() + "_" + cacheKeys[0];
		CacheNode node = cacheTypeNode.getCacheNodes().get(nodeId);
		if (node == null || cacheKeys.length <= 1)
			return node;
		else
			return searchCacheTypeNode(node, (String[]) ArrayUtils.subarray(
					cacheKeys, 1, cacheKeys.length));
	}

	/**
	 * 删除缓存
	 * 
	 * @param cacheName
	 * @param cacheNode
	 * @param cacheKey
	 * @param cascadeCacheNode
	 */
	private void removeCache(String cacheName, CacheNode cacheNode,
			String cacheKey, boolean cascadeCacheNode) {
		// 删除缓存数据
		synchronized (cacheNode) {
			try {
				getCache(cacheName).remove(cacheKey);
			} catch (Throwable t) {
				log.error(t);
			}
			// 删除节点缓存引用
			cacheNode.getCacheKeys().remove(cacheKey);
			// 级联子级节点操作
			if (cascadeCacheNode) {
				for (CacheNode node : cacheNode.getCacheNodes().values())
					removeCache(cacheName, node, cacheKey, cascadeCacheNode);
			}
		}
	}

	/**
	 * 删除节点及其子节点所有缓存和缓存记录
	 * 
	 * @param cacheName
	 * @param cacheNode
	 * @param cascadeCacheNode
	 */
	private void removeAllCache(String cacheName, CacheNode cacheNode,
			boolean cascadeCacheNode) {
		synchronized (cacheNode) {
			// 清空自己的缓存记录
			for (String cacheKey : cacheNode.getCacheKeys()) {
				try {
					getCache(cacheName).remove(cacheKey);
				} catch (Throwable t) {
					log.error(t);
				}
			}
			cacheNode.getCacheKeys().clear();
			// 级联操作
			if (cascadeCacheNode) {
				for (CacheNode node : cacheNode.getCacheNodes().values())
					removeAllCache(cacheName, node, cascadeCacheNode);
				cacheNode.getCacheNodes().clear();
			}
		}
	}

	/**
	 * 根据缓存类别和参数，生成缓存key
	 * 
	 * @param cacheName
	 * @param cacheNodes
	 * @param keyArgs
	 * @return
	 */
	private String generatorCacheKey(String cacheName, String[] cacheNodes,
			Object[] keyArgs) {
		return cacheKeyGenerator.generate(cacheName, cacheNodes, keyArgs);
	}
}
