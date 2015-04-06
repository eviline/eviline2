package org.eviline.core.conc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ActiveCompletor {
	protected Deque<CompletorTask<?>> tasks = new ArrayDeque<>();
	protected ReentrantLock lock = new ReentrantLock();
	protected Condition mutex = lock.newCondition();
	protected Executor exec;
	
	protected class CompletorTask<V> extends FutureTask<V> {
		public CompletorTask(Callable<V> callable) {
			super(callable);
		}

		@Override
		protected void done() {
			lock.lock();
			try {
				CompletorTask<?> task = tasks.poll();
				if(task != null)
					exec.execute(task);
			} finally {
				mutex.signalAll();
				lock.unlock();
			}
		}
	}
	
	public ActiveCompletor(Executor exec) {
		this.exec = exec;
	}
	
	public <V> FutureTask<V> submit(Callable<V> task) {
		CompletorTask<V> fut = new CompletorTask<>(task);
		lock.lock();
		try {
			tasks.offer(fut);
			mutex.signalAll();
		} finally {
			lock.unlock();
		}
		exec.execute(fut);
		return fut;
	}
	
	public <V> void await(Iterable<FutureTask<V>> futs) {
		for(FutureTask<?> fut : futs) {
			fut.run();
			lock.lock();
			try {
				while(!fut.isDone()) {
					CompletorTask<?> task = tasks.poll();
					if(task != null) {
						lock.unlock();
						try {
							task.run();
						} finally {
							lock.lock();
						}
					} else
						mutex.awaitUninterruptibly();
				}
			} finally {
				lock.unlock();
			}
		}
	}
}
