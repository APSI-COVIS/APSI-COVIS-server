package com.covis.Server.country.controller;

import com.covis.Server.DAO.PopulationRepository;
import com.covis.Server.Entities.CountryPopulationInfo;
import com.covis.Server.Services.CountryInfoService;
import com.covis.api.country.CountryCovidInformationResource;
import com.covis.api.country.dto.CountryPopulationDto;
import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class CountryCovidInformationController implements CountryCovidInformationResource{


    @Autowired
    CountryInfoService countryInfoService;


    @Override
    public List<CovidDailyCasesDto> listEpidemyDataInCountry(Date from, Date to, String countrySlug, CovidCasesType type) {

        try {
            return countryInfoService.getDailyCases(from,to,countrySlug,type,Boolean.FALSE);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<CovidDailyCasesDto> listEpidemyForecastInCountry(Date from, Date to, String countrySlug, CovidCasesType type){

        try {
            return countryInfoService.getDailyCases(from,to,countrySlug,type,Boolean.TRUE);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CountryPopulationDto getCountryPopulation(Date dateWhen, String countrySlug){
        return new CountryPopulationDto(countrySlug, countryInfoService.getPopulation(dateWhen,countrySlug));
    }

}
