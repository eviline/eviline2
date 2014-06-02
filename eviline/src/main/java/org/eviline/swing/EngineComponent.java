package org.eviline.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.eviline.core.Block;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public class EngineComponent extends JComponent {
	private static final long serialVersionUID = 0;
	
	protected Engine engine;
	protected double blockSizeX;
	protected double blockSizeY;
	protected boolean ghosting;
	
	protected BlockImage images;
	protected ShapeTypeColor colors;
	
	public EngineComponent(Engine engine, int blockSize, boolean simple) {
		this.engine = engine;
		blockSizeX = blockSizeY = blockSize;
		
		if(!simple) {
			try {
				images = new BlockImage(ImageIO.read(EngineTableCellRenderer.class.getResource("block.png")), (int) blockSizeX, (int) blockSizeY);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		} else
			colors = new ShapeTypeColor();
		
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
	public boolean isLightweight() {
		return true;
	}
	
	@Override
	public Dimension getPreferredSize() {
		if(isPreferredSizeSet())
			return super.getPreferredSize();
		return new Dimension((int)(blockSizeX * Field.WIDTH), (int)(blockSizeY * (Field.HEIGHT + Field.BUFFER)));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		blockSizeX = getWidth() / (double) Field.WIDTH;
		blockSizeY = getHeight() / (double) (Field.HEIGHT + Field.BUFFER);

		((Graphics2D) g).scale(blockSizeX / (int) blockSizeX, blockSizeY / (int) blockSizeY);
		blockSizeX = (int) blockSizeX;
		blockSizeY = (int) blockSizeY;
		
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
					g.fillRect((int)(x*blockSizeX), (int)((y+Field.BUFFER)*blockSizeY), (int) blockSizeX, (int) blockSizeY);
				}
				
				if(b != null) {
					ShapeType type = null;
					if(b.shape() != null)
						type = b.shape().type();
					else if((b.getFlags() & Block.MASK_GARBAGE) == Block.MASK_GARBAGE)
						type = ShapeType.G;
				
					if(type != null) {
						if(images != null) {
							g.drawImage(images.get(type), (int)(x*blockSizeX), (int)((y+Field.BUFFER) * blockSizeY), (int) blockSizeX, (int) blockSizeY, null);
						} else {
							g.setColor(colors.get(type));
							g.fillRect((int)(x*blockSizeX), (int)((y+Field.BUFFER)*blockSizeY), (int)blockSizeX, (int)blockSizeY);
						}
					}
				}
				
				if(ghost) {
					g.setColor(new Color(255,255,255));
					g.fillRect((int)(x*blockSizeX), (int)((y+Field.BUFFER)*blockSizeY), (int) blockSizeX, (int) blockSizeY);
				}

				if(b != null) {
					g.setColor(Color.BLACK);
					Block adj = (x > 0) ? engine.block(x-1, y) : b;
					if(adj == null || b.id() != adj.id())
						g.fillRect((int)(x*blockSizeX), (int)((y+Field.BUFFER)*blockSizeY), 1, (int)blockSizeY);
					adj = (x < Field.WIDTH - 1) ? engine.block(x+1, y) : b;
					if(adj == null || b.id() != adj.id())
						g.fillRect((int)((x+1)*blockSizeX)-1, (int)((y+Field.BUFFER)*blockSizeY), 1, (int)blockSizeY);
					adj = (y >= -Field.BUFFER) ? engine.block(x, y+1) : b;
					if(adj == null || b.id() != adj.id())
						g.fillRect((int)(x*blockSizeX), (int)((y+Field.BUFFER+1)*blockSizeY)-1, (int)blockSizeX, 1);
					adj = (y < Field.HEIGHT) ? engine.block(x, y-1) : b;
					if(adj == null || b.id() != adj.id())
						g.fillRect((int)(x*blockSizeX), (int)((y+Field.BUFFER)*blockSizeY), (int)blockSizeX, 1);
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
