package com.emc.code.springxd.module;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CountryCodeUtils {

	private static Map<String,String> COUNTRY_NAMES;
	
	static {
		COUNTRY_NAMES = new HashMap<String,String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<Map<String,Object>> list = mapper.readValue(CountryCodeUtils.class.getResourceAsStream("/data/country_code.json"), new TypeReference<List<Map<String,Object>>>() {});
			for (Map<String,Object> map : list) {
				COUNTRY_NAMES.put(map.get("IOC").toString(), map.get("name").toString());
			}//end for
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getCountryName(String countryCode) {
		if (COUNTRY_NAMES.containsKey(countryCode)) {
			return COUNTRY_NAMES.get(countryCode);
		}//end if
		return null;
	}
	
	public static boolean isCountryCode(String name) {
		return COUNTRY_NAMES.containsKey(name);
	}
}
