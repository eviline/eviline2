package org.eviline.core.conc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConstantFuture<V> implements Future<V> {
	protected static <V> V call(Callable<V> task) throws ExecutionException {
		try {
			return task.call();
		} catch(Exception e) {
			throw new ExecutionException(e);
		}
	}

	protected V result;
	
	public ConstantFuture(QuietCallable<V> task) {
		this(task.call());
	}
	
	public ConstantFuture(Callable<V> task) throws ExecutionException {
		this(call(task));
	}
	
	public ConstantFuture(V result) {
		this.result = result;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public V get() {
		return result;
	}

	@Override
	public V get(long timeout, TimeUnit unit) {
		return result;
	}

}
