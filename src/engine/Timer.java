package engine;

public class Timer {
	private static long[] startTime = new long[100];
	
	public static void timeStart(int i) {
		startTime[i] = System.nanoTime();
	}
	
	
	public static void timePause(int i) {
		long ptime = System.nanoTime();
		startTime[i] = ptime - startTime[i];
	}
	
	public static void timeEnd(int i, String key) {
		timeEnd(i, key, 0);
	}
	
	public static void timeEnd(int i, String key, long minDuration) {
		long etime = System.nanoTime();
		long milli = (etime - startTime[i]) / 1000000L;
		if (milli >= minDuration) {
			String timeString = milli > 1000 ? milli / 1000 + " s" : milli + " ms";
			System.out.println("( " + key + " ) time: " + timeString);
		}
	}
}
