package com.kitalulus.challenge.controller;

import com.kitalulus.challenge.dto.Country;
import com.kitalulus.challenge.dto.CountryResponse;
import com.kitalulus.challenge.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/country")
public class CountryController {

    private static final Logger LOGGER = Logger.getLogger(CountryController.class.getName());

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public CountryResponse getCountryDetailByName(@RequestParam(name = "countryName") String countryName){
        LOGGER.log(Level.INFO, "getCountryDetailByName - countryName: {0}", countryName);
        return countryService.getCountryDetailByName(countryName);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/v1/rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String GetRate(@RequestParam(name = "currency") String currency){
        LOGGER.log(Level.INFO, "getCountryDetailByName - currency: {0}", currency);
        return countryService.getRateByCurrency(currency).toString();
    }
}
