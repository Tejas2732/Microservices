package com.tejaspatil.microservices.currency_conversion_service;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
// import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
class RestClientConfiguration {

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}

@RestController
public class CurrencyConversionController {
	 @Autowired
	    private RestClient restClient;
		
	 @Autowired
	    private CurrencyExchangeProxy proxy;
		@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
		public CurrencyConversion calculateCurrencyConversion(
				@PathVariable String from,
				@PathVariable String to,
				@PathVariable BigDecimal quantity
				) {
			
			HashMap<String, String> uriVariables = new HashMap<>();
			uriVariables.put("from",from);
			uriVariables.put("to",to);

	        CurrencyConversion currencyConversion = restClient.get()
	                .uri("http://localhost:8001/currency-exchange/from/{from}/to/{to}", uriVariables)
	                .retrieve()
	                .body(CurrencyConversion.class);

	        return new CurrencyConversion(currencyConversion.getId(),
	                from, to, quantity,
	                currencyConversion.getConversionMultiple(),
	                quantity.multiply(currencyConversion.getConversionMultiple()),
	                currencyConversion.getEnvironment()+ " " + "rest client");
}

   
		
//		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity
//		("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
//				CurrencyConversion.class, uriVariables);
//		
//		CurrencyConversion currencyConversion = responseEntity.getBody();
//		
//		return new CurrencyConversion(currencyConversion.getId(), 
//				from, to, quantity, 
//				currencyConversion.getConversionMultiple(), 
//				quantity.multiply(currencyConversion.getConversionMultiple()), 
//				currencyConversion.getEnvironment()+ " " + "rest template");
        
        @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    	public CurrencyConversion calculateCurrencyConversionFeign(
    			@PathVariable String from,
    			@PathVariable String to,
    			@PathVariable BigDecimal quantity
    			) {
    		
    		

            CurrencyConversion currencyConversion =proxy.retrieveExchangeValue(from, to) ;

            return new CurrencyConversion(currencyConversion.getId(),
                    from, to, quantity,
                    currencyConversion.getConversionMultiple(),
                    quantity.multiply(currencyConversion.getConversionMultiple()),
                    currencyConversion.getEnvironment()+ " " + "feign");
		
	}

}

