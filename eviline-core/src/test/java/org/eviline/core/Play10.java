package org.eviline.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;

public class Play10 {

	public static void main(String[] args) {
		DefaultAIKernel aik = new DefaultAIKernel(new NextFitness());
		Engine engine = new Engine();
		AIPlayer ai = new AIPlayer(aik, engine, 0);

		List<Long> lines = new ArrayList<>();
		for(int i = 0; i < 20; i++) {
			engine.reset();
			while(!engine.isOver()) {
				ai.tick();
				engine.setShape(ai.getDest());
				engine.tick(Command.SHIFT_DOWN);
				ai.getCommands().clear();
				while(engine.getShape() == -1 && !engine.isOver())
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
