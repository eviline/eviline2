package org.eviline.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.eviline.core.Block;
import org.eviline.core.Engine;
import org.eviline.core.Field;

public class EngineComponent extends JComponent {
	private static final long serialVersionUID = 0;
	
	protected Engine engine;
	protected int blockSize;
	protected boolean ghosting;
	
	protected BlockImage images;
	
	public EngineComponent(Engine engine, int blockSize) {
		this.engine = engine;
		this.blockSize = blockSize;
		
		try {
			images = new BlockImage(ImageIO.read(EngineTableCellRenderer.class.getResource("block.png")), blockSize, blockSize);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		setPreferredSize(new Dimension(blockSize * Field.WIDTH, blockSize * (Field.HEIGHT + Field.BUFFER)));
		
		setFocusable(true);
		
		setRequestFocusEnabled(true);
		setVerifyInputWhenFocusTarget(false);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocusInWindow();
			}
		});
		
		setBackground(Color.BLACK);
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for(int y = -Field.BUFFER; y < Field.HEIGHT; y++) {
			for(int x = 0; x < Field.WIDTH; x++) {
				Block b = engine.block(x, y);
				boolean ghost = false;
				if(isGhosting() && b == null && engine.getGhost() != null && engine.getGhost().has(x, y))
					ghost = true;
				
				if(y < 0 && !ghost) {
					g.setColor(new Color(255,255,255,128));
					g.fillRect(x*blockSize, (y+Field.BUFFER)*blockSize, blockSize, blockSize);
				}
				
				if(b != null)
					g.drawImage(images.get(b.shape().type()), x*blockSize, (y+Field.BUFFER) * blockSize, null);
				
				if(ghost) {
					g.setColor(new Color(255,255,255));
					g.fillRect(x*blockSize, (y+Field.BUFFER)*blockSize, blockSize, blockSize);
				}
			}
		}
	}


	public boolean isGhosting() {
		return ghosting;
	}


	public void setGhosting(boolean ghosting) {
		this.ghosting = ghosting;
	}
	
}
