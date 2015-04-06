package org.eviline.core.conc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ActiveCompletor {
	protected Queue<FutureTask<?>> tasks = new ConcurrentLinkedQueue<>();
	protected Executor exec;

	public ActiveCompletor(Executor exec) {
		this.exec = exec;
	}

	public <V> FutureTask<V> submit(Callable<V> task) {
		FutureTask<V> fut = new FutureTask<>(task);
		tasks.offer(fut);
		exec.execute(fut);
		return fut;
	}

	public <V> void await(Iterable<FutureTask<V>> futs) {
		for(FutureTask<?> fut : futs) {
			fut.run();
			while(!fut.isDone()) {
				FutureTask<?> task = tasks.poll();
				if(task != null) {
					task.run();
				}
			}
		}
	}
}
