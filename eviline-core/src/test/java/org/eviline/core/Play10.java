package org.eviline.core;

import java.util.ArrayList;
import java.util.List;

import org.eviline.core.ai.AIPlayer;

public class Play10 {

	public static void main(String[] args) {
		Engine engine = new Engine();
		AIPlayer ai = new AIPlayer(engine);
		
		List<Integer> lines = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			engine.reset();
			while(!engine.isOver())
				engine.tick(ai.tick());
			lines.add(engine.getLines());
			System.out.print(".");
			System.out.flush();
		}
		System.out.println();
		System.out.println(lines);
		int sum = 0;
		for(int l : lines)
			sum += l;
		System.out.println("Average:" + (sum / lines.size()));
	}

}
