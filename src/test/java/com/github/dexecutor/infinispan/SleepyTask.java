package com.github.dexecutor.infinispan;

import java.io.Serializable;

import com.github.dexecutor.core.task.Task;

class SleepyTask extends Task<Integer, Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	public SleepyTask(final Integer id) {
		setId(id);
	}

	@Override
	public Integer execute() {
		try {
			System.out.println("Executing :*****  " +  getId());
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return getId();
	}		
}