package org.eviline.swing;

import javax.swing.table.AbstractTableModel;

import org.eviline.core.Block;
import org.eviline.core.Engine;
import org.eviline.core.Field;

public class EngineTableModel extends AbstractTableModel {
	
	protected Engine engine;
	
	public EngineTableModel(Engine engine) {
		this.engine = engine;
	}

	@Override
	public int getRowCount() {
		return Field.HEIGHT + Field.BUFFER;
	}

	@Override
	public int getColumnCount() {
		return Field.WIDTH;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Block b = engine.block(columnIndex, rowIndex - Field.BUFFER);
		if(b == null && engine.getGhost() != null) { // check ghosting
			if(engine.getGhost().has(columnIndex, rowIndex - Field.BUFFER))
				b = new Block(engine.getShape().shape(), engine.getShape().id(), Block.MASK_GHOST);
		}
		return b;
	}

}
