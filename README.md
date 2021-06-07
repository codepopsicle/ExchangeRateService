# ExchangeRateService

APIs to provide currency exchange rate services

The solution can be run in one of two ways:

1)	With Docker: If Docker is installed, then navigate to /service and execute the shell script build_image_and_run.sh. This does the following:

 Builds a Docker image (after doing a Maven build + test) and installs to local Docker repository
 Runs the Spring boot application at port 8080 on localhost

Note: Since I’m running Docker on the new Mac M1, there doesn’t yet seem to be support for either in-built Docker support for Springboot, or the Spotify Docker client plugin. This is the reason is used this manual approach.

2)	Without Maven: Navigate to /api_task and execute the command mvn spring-boot:run. This will run the Springboot application on port 8080 on localhost.



The base URL is http://localhost:8080. The list of currencies is fetched from a free API which provides rates from the ECB in JSON. This is downloaded and updated periodically by the application (1 hour at the moment) via an asynchronous scheduler.


The API resource structure is as below:

1)	/currencies

Input: Nil

 Output:

{ A mapping of available currencies to their access count}

2)	/rate/reference

 Input: 

1.	{
2.	   "base":"EUR",
3.	   "notional":"USD"
4.	}
5.	 


 Output:
	
	{reference rate of USD with rest to EUR}


3)	/rate/exchange

 Input:

1.	{
2.	   "base":"USD",
3.	   "notional":"RUB"
4.	}
5.	 


	 Output:
	
	{exchange rate of USD with respect to RUB}
               

4)	/convert

 Input:

1.		{
2.	   "base":"EUR",
3.	   "notional":"USD",
4.	   "base_amount":"100"
5.	}
6.	 


	 Output:

	{equivalent of 100 EUR}


5)	/graph

 Input:

1.	{
2.	   "currency":"RUB"
3.	}
4.	 


 Output:

{the web link to the graph for the respective currency from the ECB website}


Additionally, the API documentation will be made available at 

http://localhost:8080/swagger-ui.html


