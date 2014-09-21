package org.eviline.core;

public class EngineFactories {

	public static EngineFactory<Integer> createIntegerFactory(final Integer value) {
		return new IntegerFactory(value);
	}
	
	public static EngineFactory<ShapeSource> createSourceFactory(final Class<? extends ShapeSource> type) {
		return new ShapeSourceFactory(type);
	}
	
	private EngineFactories() {}

	public static class ShapeSourceFactory implements EngineFactory<ShapeSource> {
		private final Class<? extends ShapeSource> type;

		public ShapeSourceFactory(Class<? extends ShapeSource> type) {
			this.type = type;
		}

		@Override
		public ShapeSource newInstance(Engine e) {
			try {
				return type.newInstance();
			} catch(IllegalAccessException | InstantiationException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static class IntegerFactory implements EngineFactory<Integer> {
		private final Integer value;
	
		public IntegerFactory(Integer value) {
			this.value = value;
		}
	
		@Override
		public Integer newInstance(Engine e) {
			return value;
		}
	}
}
