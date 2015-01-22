package org.eviline.core.conc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SubtaskExecutor implements Executor {

	protected Executor executor;
	protected Deque<SyncFutureTask<?>> tasks;
	protected ReentrantLock sync;
	protected Condition mutex;

	public SubtaskExecutor(Executor executor) {
		this.executor = executor;
		tasks = new ArrayDeque<>();
		sync = new ReentrantLock();
		mutex = sync.newCondition();

	}

	@Override
	public void execute(Runnable command) {
		submit(command);
	}

	public SyncFutureTask<?> submit(Runnable task) {
		return submit(task, null);
	}

	public <V> SyncFutureTask<V> submit(Runnable task, V result) {
		return submit(Executors.callable(task, result));
	}

	public <V> SyncFutureTask<V> submit(Callable<V> task) {
		SyncFutureTask<V> future = new SyncFutureTask<V>(task);
		sync.lock();
		try {
//			if(awaiting == 0)
				executor.execute(future);
//			else {
				tasks.offer(future);
				mutex.signal();
//			}
		} finally {
			sync.unlock();
		}
		return future;
	}

	public void await(Future<?> future) throws InterruptedException {
		sync.lock();
		try {
			while(!future.isDone()) {
				if(future.isDone())
					return;
				FutureTask<?> subtask;
				subtask = tasks.poll();
				
				if(subtask != null) {
					sync.unlock();
					try {
						subtask.run();
						continue;
					} finally {
						sync.lock();
					}
				}
				if(!future.isDone() && tasks.size() == 0) {
					if(!future.isDone() && tasks.size() == 0) {
						mutex.await();
						if(!future.isDone() && tasks.size() == 0)
							mutex.signal();
					}
				}
			}
		} finally {
			sync.unlock();
		}
	}

	public void call(Runnable task) throws ExecutionException, InterruptedException {
		submit(task).get();
	}

	public <V> V call(Runnable task, V result) throws ExecutionException, InterruptedException {
		return submit(task, result).get();
	}

	public <V> V call(Callable<V> task) throws ExecutionException, InterruptedException {
		return submit(task).get();
	}

	public class SyncFutureTask<V> extends FutureTask<V> {
		protected SyncFutureTask(Callable<V> task) {
			super(task);
		}

		@Override
		protected void done() {
			sync.lock();
			try {
				mutex.signal();
			} finally {
				sync.unlock();
			}
		}

		@Override
		public V get() throws ExecutionException, InterruptedException {
			await(this);
			try {
				return super.get();
			} catch(InterruptedException e) {
				throw new Error("Impossible interrupt?", e);
			}
		}
	}

	public Executor getExecutor() {
		return executor;
	}
}
