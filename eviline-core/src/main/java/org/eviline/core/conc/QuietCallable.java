package org.eviline.core.conc;

import java.util.concurrent.Callable;

public interface QuietCallable<V> extends Callable<V> {
	@Override
	public V call();
}
