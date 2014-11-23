package org.eviline.swing;

import javax.swing.table.AbstractTableModel;

import org.eviline.core.Block;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.XYShapes;

public class EngineTableModel extends AbstractTableModel {
	
	protected Engine engine;
	protected boolean ghosting;
	
	public EngineTableModel(Engine engine) {
		this(engine, false);
	}
	
	public EngineTableModel(Engine engine, boolean ghosting) {
		this.engine = engine;
		this.ghosting = ghosting;
	}

	@Override
	public int getRowCount() {
		Field field = engine.getField();
		return field.HEIGHT + Field.BUFFER;
	}

	@Override
	public int getColumnCount() {
		Field field = engine.getField();
		return field.WIDTH;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Block b = engine.block(columnIndex, rowIndex - Field.BUFFER);
		if(isGhosting() && b == null && engine.getGhost() != -1) { // check ghosting
			if(XYShapes.has(engine.getGhost(), columnIndex, rowIndex - Field.BUFFER))
				b = new Block(XYShapes.shapeFromInt(engine.getShape()), engine.getShapeId(), Block.MASK_GHOST);
		}
		return b;
	}

	public boolean isGhosting() {
		return ghosting;
	}
	
	public void setGhosting(boolean ghosting) {
		this.ghosting = ghosting;
	}
	
}
