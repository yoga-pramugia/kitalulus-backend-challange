package com.kitalulus.challenge.dto;

import lombok.Data;

import java.util.Collection;

@Data
public class Country {

    private String nativeName;
    private int population;
    private Collection<Currency> currencies;
}
