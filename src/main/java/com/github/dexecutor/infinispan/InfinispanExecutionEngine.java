package com.github.dexecutor.infinispan;

import static com.github.dexecutor.core.support.Preconditions.checkNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;

import org.infinispan.distexec.DistributedExecutionCompletionService;
import org.infinispan.distexec.DistributedExecutorService;

import com.github.dexecutor.core.ExecutionEngine;
import com.github.dexecutor.core.graph.Dag.Node;
/**
 * 
 * @author Nadeem Mohammad
 *
 * @param <T>
 * @param <R>
 */
public final class InfinispanExecutionEngine<T, R> implements ExecutionEngine<T, R> {

	private final DistributedExecutorService executorService;
	private final CompletionService<Node<T, R>> completionService;

	public InfinispanExecutionEngine(final DistributedExecutorService executorService) {
		checkNotNull(executorService, "Executer Service should not be null");
		this.executorService = executorService;
		this.completionService = new DistributedExecutionCompletionService<Node<T, R>>(executorService);
	}

	public Future<Node<T, R>> submit(Callable<Node<T, R>> task) {
		return this.completionService.submit(task);
	}

	public Future<Node<T, R>> take() throws InterruptedException {
		return this.completionService.take();
	}

	public boolean isShutdown() {
		return this.executorService.isShutdown();
	}

	@Override
	public String toString() {
		return this.executorService.toString();
	}
}
