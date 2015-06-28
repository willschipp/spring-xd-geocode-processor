package com.emc.code.springxd.module;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * simple processor that calls http://nominatim.openstreetmap.org/search/
 * and passes through the address
 * 
 * returns a json fragment comprising the address, latitude, longitude, status and date 
 * {	
 * 	"address":[address],
 *	"latitude:[latitude],
 *	"longitude:[longitude],
 *	"status":[OK|FAILED],
 *	"updated_date":[long]
 * } 
 * @author willschipp
 *
 */
@MessageEndpoint
public class GeoCodeProcessor {
	
	private static final Log logger = LogFactory.getLog(GeoCodeProcessor.class);
	private static final String FAILED = "failed";
	private static final String OK = "ok";

	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	private String defaultURL = "http://nominatim.openstreetmap.org/search?q=";

	//address should be as a complete string
	//it will be used to invoke/call -->  http://nominatim.openstreetmap.org/search/
	//format is  http://nominatim.openstreetmap.org/search/[address string]?format=json
	@Transformer(inputChannel="input",outputChannel="output")
	public String getGeocode(String address) throws Exception {
		restTemplate = new RestTemplate();
		//encode
		String url = getUrl(address);
		//execute
		String response = restTemplate.getForObject(new URI(url), String.class);
		logger.info(response);
		//it's a JSON string, convert, enrich, reply
		List<Map<String,Object>> reply = mapper.readValue(response, new TypeReference<List<Map<String,Object>>>() { });
		Map<String,Object> processed = processResults(reply);
		if (processed.isEmpty()) {
			processed.put("status",FAILED);
		} else {
			processed.put("status", OK);
		}//end if
		processed.put("address", address);
		processed.put("updated_date",System.currentTimeMillis());
		//convert to a json string and return
		return mapper.writeValueAsString(processed);
	}
	
	private Map<String,Object> processResults(List<Map<String,Object>> results) {
		Map<String,Object> map = new HashMap<String,Object>();
		for (Map<String,Object> result : results) {
			//get the first set of latitude and longitude and add
			map.put("latitude", result.get("lat"));
			map.put("longitude", result.get("lon"));
		}//end for
		return map;
	}
	
	private String getUrl(String address) throws Exception {
		//init
		StringBuilder formattedAddress = new StringBuilder(defaultURL);
		//address format is
		//url encoded string
		//country code expanded
		//remove '-' sybols
		address = address.replaceAll("-", ",");//replace with commas
		//check if there's a country code
		String[] parts = address.split(" ");
		for (String part : parts) {
			if (CountryCodeUtils.isCountryCode(part)) {
				//convert
				address = address.replace(part, CountryCodeUtils.getCountryName(part));
			}//end if
		}//end for
		//process the address
		formattedAddress.append(URLEncoder.encode(address, "UTF-8"));
		formattedAddress.append("&format=json&addressDetails=1&polygon=0");
		//send
		return formattedAddress.toString();
		
	}
	
}
