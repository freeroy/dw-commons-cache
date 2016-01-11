package org.developerworld.commons.cache.impl.memcached;

import org.developerworld.commons.cache.Cache;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.schooner.MemCached.TransCoder;

/**
 * 缓存管理器
 * 
 * @author Roy Huang
 *
 */
public class MemcachedManager implements org.developerworld.commons.cache.CacheManager {

	// 池名
	private String poolName;
	// 池对象
	private SockIOPool sockIOPool;
	// 服务集群
	private String[] servers;
	// 服务集群权重
	private Integer[] weights;
	// 设置容错开关
	// true:当当前socket不可用时，程序会自动查找可用连接并返回，否则返回NULL
	// 默认状态是true，建议保持默认。
	private Boolean failover;
	// 初始化连接
	private Integer initConn;
	// 最小连接
	private Integer minConn;
	// 最大连接
	private Integer maxConn;
	// 最大处理时间
	private Long maxIdle;
	// 后台线程管理SocketIO池的检查间隔时间
	private Integer maintSleep;
	// 设置是否使用Nagle算法，如果是true在写数据时不缓冲，立即发送出去(默认是true）
	private Boolean nagle;
	// Socket阻塞读取数据的超时时间
	private Integer socketTO;
	// 设置socket的连接等待超时值
	private Integer socketConnectTO;
	// 设置连接心跳监测开关
	// true:每次通信都要进行连接是否有效的监测，造成通信次数倍增，加大网络负载
	// 因此在对HighAvailability要求比较高的场合应该设为true
	// 默认状态是false，建议保持默认
	private Boolean aliveCheck;
	// 缓存大小
	private Integer bufferSize;
	// 设置连接失败恢复开关
	// 设置为true，当宕机的服务器启动或中断的网络连接后，这个socket连接还可继续使用，否则将不再使用.
	// 默认状态是true，建议保持默认。
	private Boolean failback;
	// 设置hash算法,默认值为0
	// alg=0 使用String.hashCode()获得hash code,该方法依赖JDK，可能和其他客户端不兼容，建议不使用
	// alg=1 使用original 兼容hash算法，兼容其他客户端
	// alg=2 使用CRC32兼容hash算法，兼容其他客户端，性能优于original算法
	// alg=3 使用MD5 hash算法
	// 采用前三种hash算法的时候，查找cache服务器使用余数方法。采用最后一种hash算法查找cache服务时使用consistent方法。
	private Integer hashingAlg;
	// 最大繁忙时间
	private Long maxBusyTime;
	// 缓存使用的类加载器
	private ClassLoader classLoader;
	// 缓存异常处理器
	private ErrorHandler errorHandler;
	// 是否启用压缩
	private Boolean compressEnable;
	// 压缩前提容量
	private Long compressThreshold;
	// 默认编码方式
	private String defaultEncoding;
	// 是否将基本类型转换为String方法
	private Boolean primitiveAsString;
	// 当选择用URL当key的时候，MemcachedClient会自动将URL encode再存储
	private Boolean sanitizeKeys;
	// 自定义序列化传输编码器
	private TransCoder transCoder;

	private Long cacheExpiryTime;

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public void setSockIOPool(SockIOPool sockIOPool) {
		this.sockIOPool = sockIOPool;
	}

	public void setServers(String[] servers) {
		this.servers = servers;
	}

	public void setWeights(Integer[] weights) {
		this.weights = weights;
	}

	public void setFailover(Boolean failover) {
		this.failover = failover;
	}

	public void setInitConn(Integer initConn) {
		this.initConn = initConn;
	}

	public void setMinConn(Integer minConn) {
		this.minConn = minConn;
	}

	public void setMaxConn(Integer maxConn) {
		this.maxConn = maxConn;
	}

	public void setMaxIdle(Long maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMaintSleep(Integer maintSleep) {
		this.maintSleep = maintSleep;
	}

	public void setNagle(Boolean nagle) {
		this.nagle = nagle;
	}

	public void setSocketTO(Integer socketTO) {
		this.socketTO = socketTO;
	}

	public void setSocketConnectTO(Integer socketConnectTO) {
		this.socketConnectTO = socketConnectTO;
	}

	public void setAliveCheck(Boolean aliveCheck) {
		this.aliveCheck = aliveCheck;
	}

	public void setBufferSize(Integer bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setFailback(Boolean failback) {
		this.failback = failback;
	}

	public void setHashingAlg(Integer hashingAlg) {
		this.hashingAlg = hashingAlg;
	}

	public void setMaxBusyTime(Long maxBusyTime) {
		this.maxBusyTime = maxBusyTime;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void setCompressEnable(Boolean compressEnable) {
		this.compressEnable = compressEnable;
	}

	public void setCompressThreshold(Long compressThreshold) {
		this.compressThreshold = compressThreshold;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public void setPrimitiveAsString(Boolean primitiveAsString) {
		this.primitiveAsString = primitiveAsString;
	}

	public void setSanitizeKeys(Boolean sanitizeKeys) {
		this.sanitizeKeys = sanitizeKeys;
	}

	public void setTransCoder(TransCoder transCoder) {
		this.transCoder = transCoder;
	}

	public void setCacheExpiryTime(Long cacheExpiryTime) {
		this.cacheExpiryTime = cacheExpiryTime;
	}

	public MemcachedManager() {
	}

	public MemcachedManager(String poolName) {
		this(SockIOPool.getInstance(poolName));
		this.poolName = poolName;
	}

	public MemcachedManager(SockIOPool sockIOPool) {
		this.sockIOPool = sockIOPool;
	}

	public void init() {
		if (sockIOPool == null) {
			if (poolName != null)
				sockIOPool = SockIOPool.getInstance(poolName);
			else
				sockIOPool = SockIOPool.getInstance();
			if (servers != null)
				sockIOPool.setServers(servers);
			if (weights != null)
				sockIOPool.setWeights(weights);
			if (failover != null)
				sockIOPool.setFailover(failover);
			if (initConn != null)
				sockIOPool.setInitConn(initConn);
			if (minConn != null)
				sockIOPool.setMinConn(minConn);
			if (maxConn != null)
				sockIOPool.setMaxConn(maxConn);
			if (maintSleep != null)
				sockIOPool.setMaintSleep(maintSleep);
			if (nagle != null)
				sockIOPool.setNagle(nagle);
			if (socketTO != null)
				sockIOPool.setSocketTO(socketTO);
			if (aliveCheck != null)
				sockIOPool.setAliveCheck(aliveCheck);
			if (socketConnectTO != null)
				sockIOPool.setSocketConnectTO(socketConnectTO);
			if (bufferSize != null)
				sockIOPool.setBufferSize(bufferSize);
			if (failback != null)
				sockIOPool.setFailback(failback);
			if (hashingAlg != null)
				sockIOPool.setHashingAlg(hashingAlg);
			if (maxBusyTime != null)
				sockIOPool.setMaxBusyTime(maxBusyTime);
			if (maxIdle != null)
				sockIOPool.setMaxIdle(maxIdle);
		}
		// 初始化连接池
		if (!sockIOPool.isInitialized())
			sockIOPool.initialize();
	}

	public void destory() {
		if (sockIOPool != null && sockIOPool.isInitialized()) {
			sockIOPool.shutDown();
			sockIOPool = null;
		}
	}

	public Cache getCache() {
		return getCache(null);
	}

	public Cache getCache(String cacheName) {
		MemCachedClient memCachedClient = null;
		if (poolName == null)
			memCachedClient = new MemCachedClient();
		else
			memCachedClient = new MemCachedClient(poolName);
		if (classLoader != null)
			memCachedClient.setClassLoader(classLoader);
		if (errorHandler != null)
			memCachedClient.setErrorHandler(errorHandler);
		if (compressEnable != null)
			memCachedClient.setCompressEnable(compressEnable);
		if (compressThreshold != null)
			memCachedClient.setCompressThreshold(compressThreshold);
		if (defaultEncoding != null)
			memCachedClient.setDefaultEncoding(defaultEncoding);
		if (primitiveAsString != null)
			memCachedClient.setPrimitiveAsString(primitiveAsString);
		if (sanitizeKeys != null)
			memCachedClient.setSanitizeKeys(sanitizeKeys);
		if (transCoder != null)
			memCachedClient.setTransCoder(transCoder);
		if (cacheExpiryTime != null)
			return new Memcached(cacheName, memCachedClient, cacheExpiryTime);
		else
			return new Memcached(cacheName, memCachedClient);
	}
}
