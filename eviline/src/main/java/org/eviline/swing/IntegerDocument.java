package org.eviline.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument {
	public IntegerDocument() {
		setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset,
					String string, AttributeSet attr)
					throws BadLocationException {
				if(!string.matches("[0-9]+"))
					return;
				super.insertString(fb, offset, string, attr);
			}
			
			public void replace(FilterBypass fb, int offset, int length,
					String text, AttributeSet attrs)
					throws BadLocationException {
				if(!text.matches("[0-9]+"))
					return;
				super.replace(fb, offset, length, text, attrs);
			}
		});
	}
}
