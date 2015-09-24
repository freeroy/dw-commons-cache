package org.developerworld.commons.cache.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 缓存类型节点
 * 
 * @author Roy Huang
 * @version 20111009
 * 
 */
public class CacheNode {

	private String id;
	private Map<String, CacheNode> cacheNodes = new HashMap<String, CacheNode>();
	private Set<String> cacheKeys = new HashSet<String>();

	public Map<String, CacheNode> getCacheNodes() {
		return cacheNodes;
	}

	public void setCacheNodes(Map<String, CacheNode> cacheNodes) {
		this.cacheNodes = cacheNodes;
	}

	public Set<String> getCacheKeys() {
		return cacheKeys;
	}

	public void setCacheKeys(Set<String> cacheKeys) {
		this.cacheKeys = cacheKeys;
	}

	public CacheNode() {

	}

	public CacheNode(String id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "CacheTypeNode [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheNode other = (CacheNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
