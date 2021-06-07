package com.scalabale.springboot;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ResourceLoader resourceLoader;

	@Test
	public void testWelcomeMessage() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Welcome to the Exchange Rates Service !")));
	}

	@Test
	public void eurReferenceTest() throws Exception {

		/* Read input from file */
		final File inputFile = resourceLoader.getResource("classpath:reference.json").getFile();

		final String inputString = new String(Files.readAllBytes(inputFile.toPath()));

		final JSONObject inputJson = new JSONObject(inputString);

		mvc.perform(MockMvcRequestBuilders.post("/rate/reference").contentType(MediaType.APPLICATION_JSON)
			.content(inputJson.toString()))
				.andExpect(status().isOk());
	}

	@Test
	public void eurReferenceExceptionTest() throws Exception {

		/* Read input from file */
		final File inputFile = resourceLoader.getResource("classpath:reference-exception.json").getFile();

		final String inputString = new String(Files.readAllBytes(inputFile.toPath()));

		final JSONObject inputJson = new JSONObject(inputString);

		mvc.perform(MockMvcRequestBuilders.post("/rate/reference").contentType(MediaType.APPLICATION_JSON)
				.content(inputJson.toString()))
				.andExpect(status().is4xxClientError())
				.andExpect(content().string("Base currency should be EUR"));
	}

	@Test
	public void baseExchangeTest() throws Exception {

		/* Read input from file */
		final File inputFile = resourceLoader.getResource("classpath:exchange.json").getFile();

		final String inputString = new String(Files.readAllBytes(inputFile.toPath()));

		final JSONObject inputJson = new JSONObject(inputString);

		mvc.perform(MockMvcRequestBuilders.post("/rate/exchange").contentType(MediaType.APPLICATION_JSON)
				.content(inputJson.toString()))
				.andExpect(status().isOk());
	}

	@Test
	public void baseExchangeExceptionTest() throws Exception {

		/* Read input from file */
		final File inputFile = resourceLoader.getResource("classpath:exchange-exception.json").getFile();

		final String inputString = new String(Files.readAllBytes(inputFile.toPath()));

		final JSONObject inputJson = new JSONObject(inputString);

		mvc.perform(MockMvcRequestBuilders.post("/rate/exchange").contentType(MediaType.APPLICATION_JSON)
				.content(inputJson.toString()))
				.andExpect(status().is4xxClientError())
				.andExpect(content().string("Neither base currency nor notional currency should be EUR!"));
	}

	@Test
	public void conversionTest() throws Exception {

		/* Read input from file */
		final File inputFile = resourceLoader.getResource("classpath:conversion.json").getFile();

		final String inputString = new String(Files.readAllBytes(inputFile.toPath()));

		final JSONObject inputJson = new JSONObject(inputString);

		mvc.perform(MockMvcRequestBuilders.post("/convert").contentType(MediaType.APPLICATION_JSON)
				.content(inputJson.toString()))
				.andExpect(status().isOk());
	}

	/*@Test
	public void graphTest() throws Exception {

		final File inputFile = resourceLoader.getResource("classpath:graph.json").getFile();

		final String inputString = new String(Files.readAllBytes(inputFile.toPath()));

		final JSONObject inputJson = new JSONObject(inputString);

		final String matchUrl = "https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/eurofxref-graph-usd.en.html";

		final MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/graph").contentType(MediaType.APPLICATION_JSON)
				.content(inputJson.toString()))
				.andExpect(status().isOk())
				.andReturn();

		Assert.assertEquals(result.getResponse().getContentAsString(), new URL(matchUrl));
	}*/

}
