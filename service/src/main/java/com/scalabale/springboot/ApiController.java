package com.scalabale.springboot;

import com.scalabale.springboot.exception.RatesNotInitializedException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@RestController
public class ApiController {


	private ExchangeRateService exchangeRateService;

	@Autowired
	public ApiController(ExchangeRateService exchangeRateService){
		this.exchangeRateService = exchangeRateService;
	}

	@ApiOperation(value = "API greeting", notes = "")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 404, message = "Service not found"),
			@ApiResponse(code = 200, message = "Successful retrieval",
					response = String.class) })
	@RequestMapping("/")
	public String index() {
		return "Welcome to the Exchange Rates Service !";
	}

	@ApiOperation(value = "API to retrieve exchange rate for currency pair with EUR as reference", notes = "", nickname = "reference")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 404, message = "Service not found"),
			@ApiResponse(code = 200, message = "Successful retrieval",
					response = BigDecimal.class) })
	@RequestMapping(path = "/rate/reference", method = RequestMethod.POST)
	public BigDecimal getReferenceRate(@RequestBody String inputJSON) throws IllegalArgumentException, JSONException, RatesNotInitializedException {
		final String baseCurrency = new JSONObject(inputJSON).get("base").toString();
		final String notionalCurrency = new JSONObject(inputJSON).get("notional").toString();
		return exchangeRateService.getReferenceRateForCurrency(baseCurrency, notionalCurrency);
	}


	@ApiOperation(value = "API to retrieve exchange rate for currency pair with base currency as reference", notes = "", nickname = "exchange")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 404, message = "Service not found"),
			@ApiResponse(code = 200, message = "Successful retrieval",
					response = BigDecimal.class) })
	@RequestMapping(path = "/rate/exchange", method = RequestMethod.POST)
	public BigDecimal getExchangeRate(@RequestBody String inputJSON) throws IllegalArgumentException, JSONException, RatesNotInitializedException{

		final String baseCurrency = new JSONObject(inputJSON).get("base").toString();
		final String notionalCurrency = new JSONObject(inputJSON).get("notional").toString();

		return exchangeRateService.getExchangeRateForCurrency(baseCurrency, notionalCurrency);
	}

	@ApiOperation(value = "API to retrieve a map of all available currencies and how many times they were accessed", notes = "", nickname = "currencies")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 404, message = "Service not found"),
			@ApiResponse(code = 200, message = "Successful retrieval",
					response = Map.class) })
	@RequestMapping(path = "/currencies", method = RequestMethod.GET)
	public Map getAvailableCurrencies() throws RatesNotInitializedException{

		return exchangeRateService.getAvailableCurrencies();
	}

	@ApiOperation(value = "API to convert an amount of one currency into another", notes = "", nickname = "convert")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 404, message = "Service not found"),
			@ApiResponse(code = 200, message = "Successful retrieval",
					response = BigDecimal.class) })
	@RequestMapping(path = "/convert", method = RequestMethod.POST)
	public BigDecimal convertCurrencyAmount(@RequestBody String inputJSON) throws IllegalArgumentException, JSONException, RatesNotInitializedException{

		final String baseCurrency = new JSONObject(inputJSON).get("base").toString();
		final String notionalCurrency = new JSONObject(inputJSON).get("notional").toString();
		final BigDecimal baseAmount = new BigDecimal(new JSONObject(inputJSON).get("base_amount").toString());

		return exchangeRateService.convertCurrencyAmount(baseCurrency, notionalCurrency, baseAmount);
	}

	@ApiOperation(value = "API to fetch the graph of a particular currency from the ECB website", notes = "", nickname = "graph")
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "Server error"),
			@ApiResponse(code = 404, message = "Service not found"),
			@ApiResponse(code = 200, message = "Successful retrieval",
					response = URL.class) })
	@RequestMapping(path = "/graph", method = RequestMethod.POST)
	public URL getGraphURL(@RequestBody String inputJSON) throws MalformedURLException, JSONException{

		final String currency = new JSONObject(inputJSON).get("currency").toString();

		return exchangeRateService.getGraphURL(currency);
	}

}
