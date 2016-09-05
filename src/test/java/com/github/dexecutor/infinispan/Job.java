package com.github.dexecutor.infinispan;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
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
			// Create a distributed executor service using the distributed cache
			// to determine the nodes on which to run
			DefaultExecutorService distributedExecutorService = new DefaultExecutorService(
					cacheManager.getCache(cacheName));
			DefaultDependentTasksExecutor<Integer, Integer> dexecutor = newTaskExecutor(distributedExecutorService);

			buildGraph(dexecutor);
			dexecutor.execute(ExecutionBehavior.RETRY_ONCE_TERMINATING);
		}

		System.out.println("Press Enter to print the cache contents, Ctrl+D/Ctrl+Z to stop.");
	}

	private void buildGraph(DefaultDependentTasksExecutor<Integer, Integer> dexecutor) {
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

	private DefaultCacheManager createCacheManagerProgrammatically(String nodeName, String cacheName) {
		DefaultCacheManager cacheManager = new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder().transport().nodeName(nodeName)
						.addProperty("configurationFile", "jgroups.xml").build(),
				new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_SYNC).build());
		// The only way to get the "repl" cache to be exactly the same as the
		// default cache is to not define it at all
		cacheManager.defineConfiguration(cacheName,
				new ConfigurationBuilder().clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(2).build());
		return cacheManager;
	}

	private DefaultDependentTasksExecutor<Integer, Integer> newTaskExecutor(
			DistributedExecutorService executorService) {
		DependentTasksExecutorConfig<Integer, Integer> config = new DependentTasksExecutorConfig<Integer, Integer>(
				new InfinispanExecutionEngine<Integer, Integer>(executorService), new SleepyTaskProvider());
		return new DefaultDependentTasksExecutor<Integer, Integer>(config);
	}
}
