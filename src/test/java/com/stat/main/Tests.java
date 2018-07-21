package com.stat.main;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
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
//@WebMvcTest
public class Tests {

	@Autowired
	MockMvc mockMvc;
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	StatisticsService statService;
	
	@Test
	public void testReturn201ForNewTransaction() throws Exception {
		/*
		 * This test the successful addition of transaction to the list if
		 * transaction is less 60 seconds old
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

		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli() - (61 * 1000);
		Transaction oldTransaction = new Transaction(90, currentTimeStampMillis);
		String jsonTransaction = mapper.writeValueAsString(oldTransaction);

		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
				.andExpect(status().isNoContent());

	}

	@Test
	public void testGetTransactionStatistics() throws Exception {
		statService.calculateTransactionStats();
		mockMvc.perform(get("/statistics"))
		.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.sum", is(90.0)))
				.andExpect(jsonPath("$.min", is(90.0)))
				.andExpect(jsonPath("$.max", is(90.0)))
				.andExpect(jsonPath("$.avg", is(90.0)))
				.andExpect(jsonPath("$.count", is(1)))
				.andExpect(status().is(200));
	}
	
	@Test
	public void testGetTransactionStatisticsWithMutipleTransactions() throws Exception {
		
		Instant instant = Instant.now();
		long currentTimeStampMillis = instant.toEpochMilli();
		Transaction newTransaction = new Transaction(60, currentTimeStampMillis);
		String jsonTransaction = mapper.writeValueAsString(newTransaction);
		
		mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(jsonTransaction))
		.andExpect(status().isCreated());
		statService.calculateTransactionStats();
		mockMvc.perform(get("/statistics"))
		.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.sum", is(150.0)))
				.andExpect(jsonPath("$.min", is(60.0)))
				.andExpect(jsonPath("$.max", is(90.0)))
				.andExpect(jsonPath("$.avg", is(75.0)))
				.andExpect(jsonPath("$.count", is(2)))
				.andExpect(status().is(200));
	}
}
