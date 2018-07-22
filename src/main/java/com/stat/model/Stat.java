package com.stat.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*Model class in order to maintain the statistics of transaction initiated in last 60 seconds*/

public class Stat {

	private static Stat stats;
	private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
	private static final Lock readLock = readWriteLock.readLock();
	private static final Lock writeLock = readWriteLock.writeLock();
	double sum;
	double avg;
	double max;
	double min;
	long count;

	private Stat() {

	}

	// Create the task instance if it is not created already
	public static Stat getInstance() {
		readLock.lock();
		try {
			if (stats == null) {
				stats = new Stat();
			}
		} finally {
			readLock.unlock();
		}
		return stats;
	}

	public void setStats(double sum, double avg, double max, double min, long count) {
		writeLock.lock();
		try {
			this.sum = sum;
			this.avg = avg;
			// while (true) { Thread.sleep(6000); break; }
			this.max = max;
			this.min = min;
			this.count = count;
		} finally {
			writeLock.unlock();

		}

	}

	public double getSum() {
		return sum;
	}

	public double getAvg() {
		return avg;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public long getCount() {
		return count;
	}

}
