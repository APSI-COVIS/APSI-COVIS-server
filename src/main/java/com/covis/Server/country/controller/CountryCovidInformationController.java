package com.covis.Server.country.controller;

import com.covis.api.country.CountryCovidInformationResource;
import com.covis.api.country.dto.CountryPopulationDto;
import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class CountryCovidInformationController implements CountryCovidInformationResource{

    @Override
    public List<CovidDailyCasesDto> listEpidemyDataInCountry(Date from, Date to, String countrySlug, CovidCasesType type) {
        //return sample data
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
        CovidDailyCasesDto first, second;


        try {
            first = new CovidDailyCasesDto(format.parse("2020-05-05"), 100);
            second = new CovidDailyCasesDto(format.parse("2020-05-06"), 200);
        } catch (Exception e) {
            return null;
        }

        return Stream.of(first, second).collect(Collectors.toList());
    }

    @Override
    public List<CovidDailyCasesDto> listEpidemyForecastInCountry(Date from, Date to, String countrySlug, CovidCasesType type){
        //return sample data
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
        CovidDailyCasesDto first, second;

        try {
            first = new CovidDailyCasesDto(format.parse("2020-05-20"), 20);
            second = new CovidDailyCasesDto(format.parse("2020-05-21"), 10);
        } catch (Exception e) {
            return null;
        }
        return Stream.of(first, second).collect(Collectors.toList());
    }

    @Override
    public CountryPopulationDto getCountryPopulation(Date dateWhen, String countrySlug){
        //return sample data
        return new CountryPopulationDto("poland", 380000000);
    }

}
