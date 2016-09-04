package com.github.dexecutor.infinispan;

import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Test;

import com.github.dexecutor.core.DefaultDependentTasksExecutor;
import com.github.dexecutor.core.DependentTasksExecutor.ExecutionBehavior;
import com.github.dexecutor.core.DependentTasksExecutorConfig;
import com.github.dexecutor.core.TaskProvider;

public class InfinispanExecutionEngineTest {

	@Test
	public void test() {
		// Setup up a clustered cache manager
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
		// Make the default cache a distributed one
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.clustering().cacheMode(CacheMode.DIST_SYNC);
		// Initialize the cache manager
		DefaultCacheManager cacheManager = new DefaultCacheManager(global.build(), builder.build());
		// Obtain the default cache
		Cache<String, String> cache = cacheManager.getCache();
		// Create a distributed executor service using the distributed cache to
		// determine the nodes on which to run

		DefaultExecutorService executorService = new DefaultExecutorService(cache);

		try {
			DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(executorService);

			executor.addDependency(1, 2);
			executor.addDependency(1, 2);
			executor.addDependency(1, 3);
			executor.addDependency(3, 4);
			executor.addDependency(3, 5);
			executor.addDependency(3, 6);
			// executor.addDependency(10, 2); // cycle
			executor.addDependency(2, 7);
			executor.addDependency(2, 9);
			executor.addDependency(2, 8);
			executor.addDependency(9, 10);
			executor.addDependency(12, 13);
			executor.addDependency(13, 4);
			executor.addDependency(13, 14);
			executor.addIndependent(11);

			StringWriter writer = new StringWriter();
			executor.print(writer);
			System.out.println(writer);

			executor.execute(ExecutionBehavior.RETRY_ONCE_TERMINATING);
			System.out.println("*** Done ***");
		} finally {
			try {
				// Shuts down the cache manager and all associated resources
				cacheManager.stop();
				executorService.shutdownNow();
				executorService.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {

			}
		}

	}

	private DefaultDependentTasksExecutor<Integer, Integer> newTaskExecutor(DistributedExecutorService executorService) {
		DependentTasksExecutorConfig<Integer, Integer> config  = new DependentTasksExecutorConfig<Integer, Integer>(new InfinispanExecutionEngine<Integer, Integer>(executorService), new SleepyTaskProvider());
		return new DefaultDependentTasksExecutor<Integer, Integer>(config);
	}

	private static class SleepyTaskProvider implements TaskProvider<Integer, Integer> {

		public Task<Integer, Integer> provid(final Integer id) {

			return new Task<Integer, Integer>() {

				public Integer execute() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return id;
				}
			};
		}
	}
}
