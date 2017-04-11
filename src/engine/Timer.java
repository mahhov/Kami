package engine;

public class Timer {
	private static long startTime;
	
	public static void timeStart() {
		startTime = System.nanoTime();
	}
	
	public static void timeEnd(String key) {
		long etime = System.nanoTime();
		long milli = (etime - startTime) / 1000000L;
		String timeString = milli > 1000 ? milli / 1000 + " s" : milli + " ms";
		System.out.println("( " + key + " ) time: " + timeString);
	}
	
}
