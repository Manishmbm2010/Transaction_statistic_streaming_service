package com.stat.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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

	private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	List<Transaction> transactionList = new CopyOnWriteArrayList<>();
	public static final String TRANCATION_ADDED_IN_THE_LIST = "Added";
	public static final String TRANCATION_NOT_ADDED_IN_THE_LIST = "Not_Added";

	public String addTransaction(Transaction transaction) {
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();
		long transactionTimeMilli = transaction.getTimestamp();
		long timeDiff = (currentTimeStampMillis - transaction.getTimestamp()) / 1000;
		LocalDateTime transactionDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(transactionTimeMilli),
				ZoneId.systemDefault());
		LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeStampMillis),
				ZoneId.systemDefault());
		logger.info("Transction time stamp is " + transactionDateTime + " CurrentTimeStamp is " + currentDateTime);
		logger.info("Differnce between timestamp is " + timeDiff + " Seconds");

		if (timeDiff <= 60 && timeDiff>=0) {
			transactionList.add(transaction);
			logger.info("Adding transaction in List");
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
		 * updating the transaction statistics & removing the old transaction form
		 * transaction list in order to maintain constant usage of space This is how we
		 * get the O(1) time and space complexity for /statistics end point.
		 */

		Stat stat = Stat.getInstance();
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();
		// Removing transaction which are older than 60 seconds
		transactionList.removeIf(transaction -> ((currentTimeStampMillis - transaction.getTimestamp()) / 1000) > 60);
		// Getting the statistics from all available transaction in transaction List.
		DoubleSummaryStatistics summaryStats = transactionList.parallelStream()
				.map(transaction -> transaction.getAmount()).mapToDouble(amount -> amount).summaryStatistics();

		long count = summaryStats.getCount();
		stat.setCount(count);
		// if Count is zero ,then values of other statistics parameter will be zero
		if (count == 0) {
			stat.setAvg(0);
			stat.setMax(0);
			stat.setMin(0);
			stat.setSum(0);
		} else {
			stat.setAvg(summaryStats.getAverage());
			stat.setMax(summaryStats.getMax());
			stat.setMin(summaryStats.getMin());
			stat.setSum(summaryStats.getSum());
		}
	}
}
