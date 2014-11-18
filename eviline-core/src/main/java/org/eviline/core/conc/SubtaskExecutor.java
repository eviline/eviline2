package org.eviline.core.conc;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class SubtaskExecutor implements Executor {

	protected Executor executor;
	protected BlockingQueue<SyncFutureTask<?>> tasks;
	protected Object sync;
	
	public SubtaskExecutor(Executor executor) {
		this.executor = executor;
		tasks = new LinkedBlockingQueue<SyncFutureTask<?>>();
		sync = new Object();
	}
	
	@Override
	public void execute(Runnable command) {
		submit(command);
	}
	
	public Future<?> submit(Runnable task) {
		return submit(task, null);
	}

	public <V> Future<V> submit(Runnable task, V result) {
		return submit(Executors.callable(task, result));
	}

	public <V> Future<V> submit(Callable<V> task) {
		SyncFutureTask<V> future = new SyncFutureTask<V>(task);
		executor.execute(future);
		tasks.offer(future);
		synchronized(sync) {
			sync.notifyAll();
		}
		return future;
	}

	public void await(Future<?> future) throws InterruptedException {
		while(!future.isDone()) {
			FutureTask<?> subtask = tasks.poll();
			if(subtask != null) {
				subtask.run();
			} else {
				synchronized(sync) {
					while(!future.isDone() && (subtask = tasks.peek()) == null)
						sync.wait();
				}
			}
		}
	}
	
	
	public void call(Runnable task) throws InterruptedException, ExecutionException {
		submit(task).get();
	}

	public <V> V call(Runnable task, V result) throws InterruptedException, ExecutionException {
		return submit(task, result).get();
	}

	public <V> V call(Callable<V> task) throws InterruptedException, ExecutionException {
		return submit(task).get();
	}
	
	protected class SyncFutureTask<V> extends FutureTask<V> {
		public SyncFutureTask(Callable<V> task) {
			super(task);
		}
		
		@Override
		public void run() {
			super.run();
			synchronized(sync) {
				sync.notifyAll();
			}
		}
		
		@Override
		public V get() throws InterruptedException, ExecutionException {
			await(this);
			return super.get();
		}
	}
	
	public void shutdown() {
		if(executor instanceof ExecutorService)
			((ExecutorService) executor).shutdown();
	}
}
