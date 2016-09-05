package com.github.dexecutor.infinispan;

import java.io.Serializable;

import com.github.dexecutor.core.TaskProvider;

public class SleepyTaskProvider implements TaskProvider<Integer, Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	public Task<Integer, Integer> provid(final Integer id) {

		return new Task<Integer, Integer>() {

			@Override
			public Integer execute() {
				try {
					System.out.println("Executing :*****  " +  id);
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return id;
			}
		};
	}
}