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

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
