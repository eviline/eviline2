package org.eviline.core;

public interface EngineFactory<T> {
	public T newInstance(Engine e);
}
