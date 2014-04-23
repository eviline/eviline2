package org.eviline.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eviline.core.ai.AIPlayer;

public class Play10 {

	public static void main(String[] args) {
		Engine engine = new Engine();
		AIPlayer ai = new AIPlayer(engine);

		List<Long> lines = new ArrayList<>();
		for(int i = 0; i < 20; i++) {
			engine.reset();
			while(!engine.isOver()) {
				ai.tick();
				engine.setShape(ai.getDest());
				engine.tick(Command.SHIFT_DOWN);
				ai.getCommands().clear();
				while(engine.getShape() == null && !engine.isOver())
					engine.tick(Command.NOP);			
			}
			lines.add(engine.getLines());
			System.out.print(".");
			System.out.flush();
		}
		System.out.println();

		Collections.sort(lines);
		lines.remove(0);
		lines.remove(lines.size()-1);

		System.out.println(lines);

		int sum = 0;
		for(long l : lines)
			sum += l;
		System.out.println("Average:" + (sum / lines.size()));
	}

}
