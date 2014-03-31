package eviline;

import javax.swing.JFrame;

import org.eviline.core.Field;
import org.eviline.core.Shape;
import org.eviline.core.XYShape;
import org.eviline.swing.FieldTable;

public class FieldTableUI {

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		
		Field f = new Field();
		f.blit(new XYShape(Shape.T_RIGHT, 0, 0));
		
		frame.add(new FieldTable(f));
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
