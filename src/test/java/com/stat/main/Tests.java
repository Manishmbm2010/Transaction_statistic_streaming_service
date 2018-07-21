package com.stat.main;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stat.model.Transaction;
import com.stat.service.StatisticsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
// @WebMvcTest
public class Tests {

	@Autowired
	MockMvc mockMvc;
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	StatisticsService statService;

	@Test
	public void testConcurrentAccess() throws Exception {
		/*
		 * This test tries to manipulate the list while iterating the list, in order to
		 * make sure we can access list concurrently this test should be successful.
		 */
		statService.getTransactionList().clear();
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();

		Transaction newTransaction = new Transaction(60, currentTimeStampMillis);
		String jsonTransaction = mapper.writeValueAsString(newTransaction);
		// Adding 3 transactions
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isCreated());
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isCreated());
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isCreated());
		List<Transaction> transactionList = statService.getTransactionList();

		for (Transaction transaction : transactionList) {
			transactionList.remove(transaction);
		}
		int size = transactionList.size();
		assertThat(size).isEqualTo(0);
	}

	@Test
	public void testReturn201ForNewTransaction() throws Exception {
		/*
		 * Test of successful addition and return of http status code 201 if transaction
		 * time stamp is less than 60 seconds with current time stamp
		 */
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();
		Transaction newTransaction = new Transaction(90, currentTimeStampMillis);
		String jsonTransaction = mapper.writeValueAsString(newTransaction);
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isCreated());

	}

	@Test
	public void testReturn204ForOldTransaction() throws Exception {
		/*
		 * Test of return Http status 204 if transaction time stamp is older than last
		 * 60 seconds.
		 */
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli() - (61 * 1000);
		Transaction oldTransaction = new Transaction(90, currentTimeStampMillis);
		String jsonTransaction = mapper.writeValueAsString(oldTransaction);

		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isNoContent());

	}

	@Test
	public void testUpdatesInTransactionStatisticWithTime() throws Exception {

		/*
		 * This test checks the returned statistics of transactions are correct or not,
		 * In this test we insert multiple transactions and update the statistic and
		 * finally compare the statistic, Then we sleep for 61 seconds and see whether
		 * the inserted transaction  is deleted and statisticF are updated accordingly or not 
		 */
		statService.getTransactionList().clear();
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();

		// Transaction 1
		Transaction newTransaction = new Transaction(60, currentTimeStampMillis);
		String jsonTransaction = mapper.writeValueAsString(newTransaction);
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isCreated());

		statService.calculateTransactionStats();
		mockMvc.perform(get("/statistics")).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.sum", is(60.0))).andExpect(jsonPath("$.min", is(60.0)))
				.andExpect(jsonPath("$.max", is(60.0))).andExpect(jsonPath("$.avg", is(60.0)))
				.andExpect(jsonPath("$.count", is(1))).andExpect(status().is(200));

		Thread.sleep(60001);
		statService.calculateTransactionStats();
		mockMvc.perform(get("/statistics")).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.sum", is(0.0))).andExpect(jsonPath("$.min", is(0.0)))
				.andExpect(jsonPath("$.max", is(0.0))).andExpect(jsonPath("$.avg", is(0.0)))
				.andExpect(jsonPath("$.count", is(0))).andExpect(status().is(200));
	}

	@Test
	public void testGetTransactionStatistics() throws Exception {

		/*
		 * This test checks the returned statistics of transactions are correct or not,
		 * In this test we insert multiple transactions and update the statistic and
		 * finally compare the statistic
		 */
		statService.getTransactionList().clear();
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();

		// Transaction 1
		Transaction newTransaction = new Transaction(60, currentTimeStampMillis);
		String jsonTransaction = mapper.writeValueAsString(newTransaction);
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isCreated());

		// Transaction 2
		newTransaction = new Transaction(90, currentTimeStampMillis);
		jsonTransaction = mapper.writeValueAsString(newTransaction);
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isCreated());

		statService.calculateTransactionStats();
		mockMvc.perform(get("/statistics")).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.sum", is(150.0))).andExpect(jsonPath("$.min", is(60.0)))
				.andExpect(jsonPath("$.max", is(90.0))).andExpect(jsonPath("$.avg", is(75.0)))
				.andExpect(jsonPath("$.count", is(2))).andExpect(status().is(200));
	}
}
