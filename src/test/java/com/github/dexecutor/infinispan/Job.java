package com.github.dexecutor.infinispan;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.github.dexecutor.core.DefaultDependentTasksExecutor;
import com.github.dexecutor.core.DependentTasksExecutor.ExecutionBehavior;
import com.github.dexecutor.core.DependentTasksExecutorConfig;

public class Job {
	
	public void run(boolean isMaster, String nodeName, String cacheName) throws Exception {
		EmbeddedCacheManager cacheManager = createCacheManagerProgrammatically(nodeName, cacheName);
		final Cache<String, String> cache = cacheManager.getCache(cacheName);

		System.out.printf("Cache %s started on %s, cache members are now %s\n", cacheName, cacheManager.getAddress(),
				cache.getAdvancedCache().getRpcManager().getMembers());

		cache.addListener(new LoggingListener());

		if (isMaster) {
			DefaultExecutorService distributedExecutorService = new DefaultExecutorService(cache);
			DefaultDependentTasksExecutor<Integer, Integer> dexecutor = newTaskExecutor(distributedExecutorService);

			buildGraph(dexecutor);
			dexecutor.execute(ExecutionBehavior.TERMINATING);
		}

		System.out.println("Press Enter to print the cache contents, Ctrl+D/Ctrl+Z to stop.");
	}

	private void buildGraph(final DefaultDependentTasksExecutor<Integer, Integer> dexecutor) {
		dexecutor.addDependency(1, 2);
		dexecutor.addDependency(1, 2);
		dexecutor.addDependency(1, 3);
		dexecutor.addDependency(3, 4);
		dexecutor.addDependency(3, 5);
		dexecutor.addDependency(3, 6);
		dexecutor.addDependency(2, 7);
		dexecutor.addDependency(2, 9);
		dexecutor.addDependency(2, 8);
		dexecutor.addDependency(9, 10);
		dexecutor.addDependency(12, 13);
		dexecutor.addDependency(13, 4);
		dexecutor.addDependency(13, 14);
		dexecutor.addIndependent(11);
	}

	private DefaultCacheManager createCacheManagerProgrammatically(final String nodeName, final String cacheName) {
		DefaultCacheManager cacheManager = new DefaultCacheManager(globalConfiguration(nodeName), defaultConfiguration());
		cacheManager.defineConfiguration(cacheName, cacheConfiguration());
		return cacheManager;
	}

	private GlobalConfiguration globalConfiguration(String nodeName) {
		return GlobalConfigurationBuilder
					.defaultClusteredBuilder()
					.transport()
					.nodeName(nodeName)
				    .addProperty("configurationFile", "jgroups.xml")
				    .build();
	}

	private Configuration defaultConfiguration() {
		return new ConfigurationBuilder()
					.clustering()
					.cacheMode(CacheMode.REPL_SYNC)
					.build();
	}

	private Configuration cacheConfiguration() {
		return new ConfigurationBuilder()
					.clustering()
					.cacheMode(CacheMode.DIST_SYNC)
					.hash()
					.numOwners(2)
					.build();
	}

	private DefaultDependentTasksExecutor<Integer, Integer> newTaskExecutor(final DistributedExecutorService executorService) {
		return new DefaultDependentTasksExecutor<Integer, Integer>(taskExecutorConfig(executorService));
	}

	private DependentTasksExecutorConfig<Integer, Integer> taskExecutorConfig(final DistributedExecutorService executorService) {
		return new DependentTasksExecutorConfig<Integer, Integer>(executionEngine(executorService), new SleepyTaskProvider());
	}

	private InfinispanExecutionEngine<Integer, Integer> executionEngine(final DistributedExecutorService executorService) {
		return new InfinispanExecutionEngine<Integer, Integer>(executorService);
	}
}
