package com.github.dexecutor.infinispan;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;

public class SleepyTaskProvider implements TaskProvider<Integer, Integer> {

	public Task<Integer, Integer> provideTask(final Integer id) {
		return new SleepyTask(id);
	}
}