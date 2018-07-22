package com.stat.model;

/*Model class in order to maintain the statistics of transaction initiated in last 60 seconds*/
public class Stat {

	private static Stat stats;
	double sum;
	double avg;
	double max;
	double min;
	long count;

	private Stat() {

	}

	// Create the task instance if it is not created already
	public static Stat getInstance() {
		if (stats == null) {
			stats = new Stat();
		}
		return stats;
	}

	public synchronized void setStats(double sum, double avg, double max, double min, long count) {
		this.sum = sum;
		this.avg = avg;
		/*
		 * while (true) {
		 * 
		 * Thread.sleep(60000); break; }
		 */
		this.max = max;
		this.min = min;
		this.count = count;

	}

	public synchronized double getSum() {
		return sum;
	}

	public synchronized double getAvg() {
		return avg;
	}

	public synchronized double getMax() {
		return max;
	}

	public synchronized double getMin() {
		return min;
	}

	public synchronized long getCount() {
		return count;
	}

}
