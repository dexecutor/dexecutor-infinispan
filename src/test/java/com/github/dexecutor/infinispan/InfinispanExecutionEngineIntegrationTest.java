package com.github.dexecutor.infinispan;

import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

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

public class InfinispanExecutionEngineIntegrationTest {

	@Test
	public void testDistrutedExecutorService() {

		DefaultCacheManager cacheManager = buildCacheManager();
		
		// Create a distributed executor service using the distributed cache to determine the nodes on which to run
		DefaultExecutorService distributedExecutorService = new DefaultExecutorService(cacheManager.getCache());

		try {
			DefaultDependentTasksExecutor<Integer, Integer> dexecutor = newTaskExecutor(distributedExecutorService);

			dexecutor.addDependency(1, 2);
			dexecutor.addDependency(1, 2);
			dexecutor.addDependency(1, 3);
			dexecutor.addDependency(3, 4);
			dexecutor.addDependency(3, 5);
			dexecutor.addDependency(3, 6);
			// executor.addDependency(10, 2); // cycle
			dexecutor.addDependency(2, 7);
			dexecutor.addDependency(2, 9);
			dexecutor.addDependency(2, 8);
			dexecutor.addDependency(9, 10);
			dexecutor.addDependency(12, 13);
			dexecutor.addDependency(13, 4);
			dexecutor.addDependency(13, 14);
			dexecutor.addIndependent(11);

			printGraph(dexecutor);

			dexecutor.execute(ExecutionBehavior.RETRY_ONCE_TERMINATING);
			System.out.println("*** Done ***");
		} finally {
			try {
				// Shuts down the cache manager and all associated resources
				cacheManager.stop();
				distributedExecutorService.shutdownNow();
				distributedExecutorService.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {

			}
		}
	}

	private void printGraph(DefaultDependentTasksExecutor<Integer, Integer> dexecutor) {
		StringWriter writer = new StringWriter();
		dexecutor.print(writer);
		System.out.println(writer);
	}

	private DefaultCacheManager buildCacheManager() {
		// Setup up a clustered cache manager
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
		// Make the default cache a distributed one
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.clustering().cacheMode(CacheMode.DIST_SYNC);
		// Initialize the cache manager
		DefaultCacheManager cacheManager = new DefaultCacheManager(global.build(), builder.build());
		// Obtain the default cache
		return cacheManager;
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
