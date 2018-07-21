package com.stat.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.stat.model.Stat;
import com.stat.model.Transaction;
import com.stat.service.StatisticsService;

@RestController
public class Controller {

	@Autowired
	StatisticsService statService;

	@PostMapping("transactions")
	public ResponseEntity<String> addTransaction(@RequestBody Transaction transaction) {
		/*
		 * Controller function to add the transaction in List if transaction time stamp
		 * is not older than 60 seconds
		 */
		String confirmation = statService.addTransaction(transaction);
		if (confirmation.equals(StatisticsService.TRANCATION_ADDED_IN_THE_LIST)) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else if (confirmation.equals(StatisticsService.TRANCATION_NOT_ADDED_IN_THE_LIST)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// @RequestMapping(method = RequestMethod.GET, value="​transactions")
	@GetMapping("transactions")
	public List<Transaction> getAllTransaction() {
		/*
		 * Return all the available transaction form the transaction list , this list
		 * content is dynamic and keep on changing based on transaction time stamp. If
		 * transaction time stamp is older than 60 seconds it will be deleted
		 */
		return statService.getTransactionList();
	}

	// @RequestMapping(method = RequestMethod.GET, value="​statistics")
	@GetMapping("statistics")
	public Stat getStatistics() {
		/*
		 * This return the statistics of all available transaction in transaction list.
		 * It include Total number of transaction, Min, Max, Avg and total amount of all
		 * the transactions initiated in last 60 seconds
		 */
		return statService.getStats();
	}
}
