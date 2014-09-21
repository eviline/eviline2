package org.eviline.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EngineFactories {

	public static EngineFactory<Integer> createIntegerFactory(final Integer value) {
		return new IntegerFactory(value);
	}
	
	public static EngineFactory<ShapeSource> createSourceFactory(final Class<? extends ShapeSource> type) {
		return new ShapeSourceFactory(type, new Class<?>[0], new Object[0]);
	}
	
	private EngineFactories() {}

	public static class ShapeSourceFactory implements EngineFactory<ShapeSource> {
		private Class<? extends ShapeSource> type;
		private Class<?>[] argTypes;
		private Object[] argValues;

		public ShapeSourceFactory(Class<? extends ShapeSource> type, Class<?>[] argTypes, Object[] argValues) {
			this.type = type;
			this.argTypes = argTypes;
			this.argValues = argValues;
		}

		@Override
		public ShapeSource newInstance(Engine e) {
			try {
				return type.getConstructor(argTypes).newInstance(argValues);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static class IntegerFactory implements EngineFactory<Integer> {
		private Integer value;
	
		public IntegerFactory(Integer value) {
			this.value = value;
		}
	
		@Override
		public Integer newInstance(Engine e) {
			return value;
		}
	}
}
