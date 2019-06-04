package thebetweenlands.util;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public final class TimeMeasurement {

	private static final HashMap<String, Long> times = new HashMap<String, Long>();
	private static final HashMap<String, Counter> counters = new HashMap<String, Counter>();

	public static void start(String id) {
		System.out.println("Started '" + id);
		times.put(id, System.nanoTime());
	}

	public static void finish(String id) {
		if (!times.containsKey(id))
			return;
		long time = System.nanoTime() - times.remove(id);
		if (time >= 10000000)
			;
		System.out.println("Finished '" + id + "' in " + time + " ns (" + TimeUnit.NANOSECONDS.toMillis(time) + " ms)");

		Counter counter = counters.get(id);
		if (counter == null)
			counters.put(id, counter = new Counter());
		if (counter.add(time))
			// System.out.println("Average time for '"+id+"' is "+avg+" ns ("+TimeUnit.NANOSECONDS.toMillis(avg)+" ms)");
			counter.reset();
	}

	private static final class Counter {

		private short counter;

		public boolean add(long time) {
			return ++counter > 256;
		}

		public void reset() {
			counter = 0;
		}
	}
}
