package com.covis.Server.Services;


import com.covis.Server.DAO.MainRepository;
import com.covis.Server.DAO.PopulationRepository;
import com.covis.Server.Entities.CountryPopulationInfo;
import com.covis.Server.Entities.DatabaseRecord;
import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CountryInfoService {

    @Autowired
    MainRepository mainRepository;
    @Autowired
    PopulationRepository populationRepository;


    public CountryInfoService(){

    }


    public List<CovidDailyCasesDto> getDailyCases(Date from, Date to, String countrySlug, CovidCasesType type, Boolean isForecast){
        List<CovidDailyCasesDto> returnValue = null;
        switch (type){
            case DEATH:
                returnValue = isForecast ? getCountryDailyDeathsForecast(from,to,countrySlug):getCountryDailyDeaths(from,to,countrySlug);
                break;
            case RECOVERED:
                returnValue = isForecast ? getCountryDailyRecoveredForecast(from,to,countrySlug) : getCountryDailyRecovered(from,to,countrySlug);
                break;
            case NEW:
                returnValue = isForecast ? getCountryDailyConfirmedForecast(from,to,countrySlug) : getCountryDailyConfirmed(from,to,countrySlug);
                break;
            case ACTIVE:
                returnValue = isForecast ? getCountryDailyActiveForecast(from,to,countrySlug) : getCountryDailyActive(from,to,countrySlug);
                break;
        }
        return returnValue;
    }

    public Integer getPopulation(Date date, String countrySlug){
        Optional<CountryPopulationInfo> x = populationRepository.findOneByCountrySlug(countrySlug);
        return x.isPresent() ? x.get().getPopulation() : -1;
    }

    private List<CovidDailyCasesDto> getCountryDailyDeaths(Date from, Date to, String countrySlug){
        Date oneDayBeforeFrom = Date.from(from.toInstant().minus(Duration.ofDays(1)));
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetween(countrySlug, from, to)){
            tempRecord.ifPresentOrElse(record -> returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), databaseRecord.getDeaths() - record.getDeaths())), () -> returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), 0)));
            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyConfirmed(Date from, Date to, String countrySlug){
        Date oneDayBeforeFrom = Date.from(from.toInstant().minus(Duration.ofDays(1)));
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetween(countrySlug, from, to)){
            tempRecord.ifPresentOrElse(record -> returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), databaseRecord.getConfirmed() - record.getConfirmed())), () -> returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), 0)));
            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyRecovered(Date from, Date to, String countrySlug){
        Date oneDayBeforeFrom = Date.from(from.toInstant().minus(Duration.ofDays(1)));
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetween(countrySlug, from, to)){
            tempRecord.ifPresentOrElse(record -> returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), databaseRecord.getRecovered() - record.getRecovered())), () -> returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), 0)));
            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyActive(Date from, Date to, String countrySlug){

        return mainRepository.findAllByCountryNameAndDateBetween(countrySlug,from,to).stream().map(
                elem -> new CovidDailyCasesDto(elem.getDate(), elem.getConfirmed()-elem.getDeaths()-elem.getRecovered())
        ).collect(Collectors.toList());
    }

    private List<CovidDailyCasesDto> getCountryDailyActiveForecast(Date from, Date to, String countrySlug){
        return null;//TODO

    }

    private List<CovidDailyCasesDto> getCountryDailyRecoveredForecast(Date from, Date to, String countrySlug){
        return null;//TODO

    }

    public List<CovidDailyCasesDto> getCountryDailyDeathsForecast(Date from, Date to, String countrySlug){
        return null;//TODO

    }

    private List<CovidDailyCasesDto> getCountryDailyConfirmedForecast(Date from, Date to, String countrySlug){
        return null;//TODO

    }

}
