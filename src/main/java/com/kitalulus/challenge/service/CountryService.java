package com.kitalulus.challenge.service;

import com.kitalulus.challenge.dto.Country;
import com.kitalulus.challenge.dto.CountryResponse;
import com.kitalulus.challenge.entity.CountryRate;
import com.kitalulus.challenge.repository.CountryRateRepo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CountryService {

    private static final Logger LOGGER = Logger.getLogger(CountryService.class.getName());

    private final RestTemplate restTemplate;

    private final CountryRateRepo countryRateRepo;

    public CountryService(RestTemplate restTemplate, CountryRateRepo countryRateRepo) {
        this.restTemplate = restTemplate;
        this.countryRateRepo = countryRateRepo;
    }

    private static final String COUNTRY_API_URI = "https://restcountries.com/v2/name/";
    private static final String RATES_API_URI = "http://data.fixer.io/api/latest";
    private static final String RATES_API_KEY = "16a9cef5b504a535b5b64b0071881c37";

    @Transactional
    public CountryResponse getCountryDetailByName(String name) {
        LOGGER.log(Level.INFO, "Start - getCountryDetailByName with country : {0}", name);
        Country country = getCountryByAPI(name);
        Double rate = getRateByCurrency(country.getCurrencies().iterator().next().getCode());

        CountryRate countryRate = new CountryRate();
        countryRate.setCountryName(country.getNativeName());
        countryRate.setRate(rate);
        countryRateRepo.save(countryRate);

        CountryResponse response = new CountryResponse();
        response.setFullName(country.getNativeName());
        response.setPopulation(country.getPopulation());
        response.setRate(rate);

        return response;
    }

    public Country getCountryByAPI(String name) {
        LOGGER.log(Level.INFO, "Start - getCountryByAPI with country : {0}", name);
        Country result = new Country();
        String uri = COUNTRY_API_URI + name;
        try {
            ResponseEntity<Country[]> response = restTemplate.getForEntity(uri, Country[].class);
            for (Country res : Objects.requireNonNull(response.getBody())) {
                result = res;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Couldn't get data from url {0}", uri);
        }
        return result;
    }

    public Double getRateByCurrency(String currency) {
        //currently, only convert EUR into currency
        LOGGER.log(Level.INFO, "Start - getRateByCurrency with currency : {0}", currency);
        Double result = null;
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(RATES_API_URI)
                // Add query parameter
                .queryParam("access_key", RATES_API_KEY)
                .queryParam("symbols", currency);

        ParameterizedTypeReference<LinkedHashMap<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        RequestEntity<Void> request = RequestEntity.get(builder.toUriString()).accept(MediaType.APPLICATION_JSON).build();
        try {
            LinkedHashMap<String, Object> response = restTemplate.exchange(request, responseType).getBody();
            if (response != null && response.get("rates") != null) {
                Map<String, Double> res = (Map<String, Double>) response.get("rates");
                result = res.get(currency);
            }
        } catch (RestClientException ex) {
            LOGGER.log(Level.SEVERE, "Couldn't get data from url : {0}", RATES_API_URI);
        }
        return result;
    }
}
