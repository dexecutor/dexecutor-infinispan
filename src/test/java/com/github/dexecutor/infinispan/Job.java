/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import com.github.dexecutor.core.DefaultDexecutor;
import com.github.dexecutor.core.DexecutorConfig;
import com.github.dexecutor.core.ExecutionConfig;

public class Job {
	
	public void run(boolean isMaster, String nodeName, String cacheName) throws Exception {
		EmbeddedCacheManager cacheManager = createCacheManagerProgrammatically(nodeName, cacheName);
		final Cache<String, String> cache = cacheManager.getCache(cacheName);

		System.out.printf("Cache %s started on %s, cache members are now %s\n", cacheName, cacheManager.getAddress(),
				cache.getAdvancedCache().getRpcManager().getMembers());

		cache.addListener(new LoggingListener());

		if (isMaster) {
			DefaultExecutorService distributedExecutorService = new DefaultExecutorService(cache);
			DefaultDexecutor<Integer, Integer> dexecutor = newTaskExecutor(distributedExecutorService);

			buildGraph(dexecutor);
			dexecutor.execute(ExecutionConfig.TERMINATING);
		}

		System.out.println("Press Enter to print the cache contents, Ctrl+D/Ctrl+Z to stop.");
	}

	private void buildGraph(final DefaultDexecutor<Integer, Integer> dexecutor) {
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

	private DefaultDexecutor<Integer, Integer> newTaskExecutor(final DistributedExecutorService executorService) {
		return new DefaultDexecutor<Integer, Integer>(taskExecutorConfig(executorService));
	}

	private DexecutorConfig<Integer, Integer> taskExecutorConfig(final DistributedExecutorService executorService) {
		return new DexecutorConfig<Integer, Integer>(executionEngine(executorService), new SleepyTaskProvider());
	}

	private InfinispanExecutionEngine<Integer, Integer> executionEngine(final DistributedExecutorService executorService) {
		return new InfinispanExecutionEngine<Integer, Integer>(executorService);
	}
}
