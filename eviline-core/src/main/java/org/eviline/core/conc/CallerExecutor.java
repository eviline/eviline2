package org.eviline.core.conc;

import java.util.concurrent.Executor;

public class CallerExecutor implements Executor {
	private static final CallerExecutor instance = new CallerExecutor();
	public static CallerExecutor get() {
		return instance;
	}
	
	@Override
	public void execute(Runnable command) {
		try {
			command.run();
		} catch(Throwable t) {
		}
	}
}
