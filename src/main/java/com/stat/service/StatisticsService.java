package com.stat.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.stat.model.Stat;
import com.stat.model.Transaction;

@Service
public class StatisticsService {

	public static final String TRANCATION_ADDED_IN_THE_LIST = "Added";
	public static final String TRANCATION_NOT_ADDED_IN_THE_LIST = "Not_Added";

	private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
	List<Transaction> transactionList = new CopyOnWriteArrayList<>();

	public String addTransaction(Transaction transaction) {
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();
		long transactionTimeMilli = transaction.getTimestamp();
		double timeDiff = (double) (currentTimeStampMillis - transactionTimeMilli) / 1000;
		LocalDateTime transactionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(transactionTimeMilli),
				ZoneId.systemDefault());
		LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeStampMillis),
				ZoneId.systemDefault());
		logger.info("Transaction time stamp is " + transactionDateTime + " CurrentTimeStamp is " + currentDateTime);
		logger.info("Differnce between timestamp is " + timeDiff + " Seconds");

		if (timeDiff <= 60 && timeDiff >= 0) {
			transactionList.add(transaction);
			logger.info("Adding transaction in the List");
			return TRANCATION_ADDED_IN_THE_LIST;
		} else {
			return TRANCATION_NOT_ADDED_IN_THE_LIST;
		}
	}

	public List<Transaction> getTransactionList() {
		logger.info("Inside get Transaction List function of Stat service");
		return transactionList;
	}

	public Stat getStats() {
		/*
		 * This function return the latest updated transaction statistics, Transactions
		 * statistics are getting updated by calculateTransactionStats function.
		 */
		logger.info("Inside GetStats Function in Statistics service");
		return Stat.getInstance();
	}

	/*
	 * Fix delay defined the minimum amount of delay between "end of one iteration"
	 * and "beginning of another iteration".
	 */

	@Scheduled(fixedDelay = 1)
	public void calculateTransactionStats() {
		/*
		 * Introducing delay of 1 millisecond between every iteration of below function
		 * This is kind of scheduled daemon, which stream transactions and keeps on
		 * updating the transaction statistics & removing the old transaction from
		 * transaction list in order to maintain constant usage of space. This is how we
		 * get the O(1) time and space complexity for /statistics end point.
		 */
		logger.debug("Started Statistics Calculations");
		Stat stat = Stat.getInstance();
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();
		// Removing transaction which are older than 60 seconds
		transactionList
				.removeIf(transaction -> ((double) (currentTimeStampMillis - transaction.getTimestamp()) / 1000) > 60);
		// Getting the statistics from all available transaction in transaction List.
		DoubleSummaryStatistics summaryStats = transactionList.parallelStream()
				.map(transaction -> transaction.getAmount()).mapToDouble(amount -> amount).summaryStatistics();

		long count = summaryStats.getCount();
		// if Count is zero ,then values of other statistics parameter will be zero
		if (count == 0) {
			stat.setStats(0, 0, 0, 0, 0);
		} else {
			stat.setStats(summaryStats.getSum(), summaryStats.getAverage(), summaryStats.getMax(),
					summaryStats.getMin(), summaryStats.getCount());
		}
	}
}
