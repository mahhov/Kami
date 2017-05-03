package engine;

import java.io.PrintWriter;

public class Timer {
	public static final Time WORLD_CONSTRUCTOR = new Time("World Constructor", 0, true);
	public static final Time BACKGROUND = new Time("Background", 10, true);
	public static final Time CHUNKS = new Time("Chunks", 70, true);
	public static final Time INTERFACE = new Time("Interface", 10, true);
	public static final Time CELL_DRAW_AGGREGATED = new Time("Cell.draw aggregated", 40, true);
	public static final Time TO_CAMERA_AGGREGATED = new Time("toCamera aggregated", 20, true);
	public static final Time PAINTER_QUEUE_PAINT = new Time("painterQueue.paint()", 10, true);
	public static final Time PAINT = new Time("paint()", 10, true);
	public static final Time LOOP_1 = new Time("loop 1", 20, true);
	public static final Time WORLD_DRAW = new Time("world.draw", 100, true);
	public static final Time FIND = new Time("find", 100, true);
	public static final Time TERRAIN_CONSTRUCTOR = new Time("Terrain Constructor", 0, true);
	public static final Time EXPAND = new Time("expand", 20, true);
	public static final Time ADD_TO_WORLD = new Time("add to world", 10, true);
	
	static void writeFile() {
		String s = "";
		s += WORLD_CONSTRUCTOR.toFileString();
		s += BACKGROUND.toFileString();
		s += CHUNKS.toFileString();
		s += INTERFACE.toFileString();
		s += CELL_DRAW_AGGREGATED.toFileString();
		s += TO_CAMERA_AGGREGATED.toFileString();
		s += PAINTER_QUEUE_PAINT.toFileString();
		s += PAINT.toFileString();
		s += LOOP_1.toFileString();
		s += WORLD_DRAW.toFileString();
		s += FIND.toFileString();
		s += TERRAIN_CONSTRUCTOR.toFileString();
		s += EXPAND.toFileString();
		s += ADD_TO_WORLD.toFileString();
		
		try {
			PrintWriter out = new PrintWriter("timelog");
			out.write(s);
			out.close();
			System.out.println("timerlog written");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static class Time {
		private String name;
		private long startTime, avgTime, minDuration;
		private int count;
		private boolean write;
		
		private Time(String name, long minDuration, boolean write) {
			this.name = name;
			this.minDuration = minDuration;
			this.write = write;
		}
		
		public void timeStart() {
			startTime = System.nanoTime();
		}
		
		
		public void timePause() {
			long pauseTime = System.nanoTime();
			startTime = pauseTime - startTime;
		}
		
		public void timeEnd() {
			long endTime = System.nanoTime();
			long milli = (endTime - startTime) / 1000000L;
			avgTime += milli;
			count++;
			if (milli >= minDuration)
				System.out.println(toString(milli));
		}
		
		private String toString(long milli) {
			return "( " + name + " ) time: " + (milli > 1000 ? milli / 1000 + " s" : milli + " ms");
		}
		
		private String toFileString() {
			return write ? toString(avgTime / count) + "\n" : "";
		}
	}
}
