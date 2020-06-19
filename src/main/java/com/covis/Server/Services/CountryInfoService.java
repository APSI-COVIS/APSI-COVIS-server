package com.covis.Server.Services;


import com.covis.Server.DAO.MainRepository;
import com.covis.Server.DAO.PopulationRepository;
import com.covis.Server.Entities.CountryPopulationInfo;
import com.covis.Server.Entities.DatabaseRecord;
import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CountryInfoService {

    @Autowired
    MainRepository mainRepository;
    @Autowired
    PopulationRepository populationRepository;


    public CountryInfoService(){

    }

    private Date addOneDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }
    public List<CovidDailyCasesDto> getDailyCases(Date from, Date to, String countryCode, CovidCasesType type, Boolean isForecast){
        String countrySlug = populationRepository.findOneByCountrySlug(countryCode).get().getCountryName();
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
            if(tempRecord.isPresent()){
                returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), databaseRecord.getDeaths() - tempRecord.get().getDeaths()));
            } else{
                returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), 0));
            }
            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyConfirmed(Date from, Date to, String countrySlug){
        Date oneDayBeforeFrom = Date.from(from.toInstant().minus(Duration.ofDays(1)));
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetween(countrySlug, from, to)){
            if(tempRecord.isPresent()){
                returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), databaseRecord.getConfirmed() - tempRecord.get().getConfirmed()));
            } else{
                returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), 0));
            }

            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyRecovered(Date from, Date to, String countrySlug){
        Date oneDayBeforeFrom = Date.from(from.toInstant().minus(Duration.ofDays(1)));
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetween(countrySlug, from, to)){
            if(tempRecord.isPresent()){
                returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), databaseRecord.getRecovered() - tempRecord.get().getRecovered()));
            } else{
                returnList.add(new CovidDailyCasesDto(databaseRecord.getDate(), 0));
            }
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
        //TODO implement forecast
        List<DatabaseRecord> databaseRecord = mainRepository.findTop2ByCountryNameOrderByDateDesc(countrySlug);
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        DatabaseRecord record = databaseRecord.get(0);
        int magicIncrement = 16;
        int x = record.getConfirmed()-record.getDeaths()-record.getRecovered();
        while(to.compareTo(from) > 0){
            returnList.add(new CovidDailyCasesDto(from, x));
            from = addOneDay(from);
            x+=magicIncrement;
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyRecoveredForecast(Date from, Date to, String countrySlug){
        //TODO implement forecast
        List<DatabaseRecord> databaseRecordList = mainRepository.findTop2ByCountryNameOrderByDateDesc(countrySlug);
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        int magicIncrement = 1;
        int x = databaseRecordList.get(0).getRecovered()-databaseRecordList.get(1).getRecovered();
        while(to.compareTo(from) > 0){
            returnList.add(new CovidDailyCasesDto(from, x));
            from = addOneDay(from);
            x+=magicIncrement;
        }
        return returnList;

    }

    public List<CovidDailyCasesDto> getCountryDailyDeathsForecast(Date from, Date to, String countrySlug){
        //TODO implement forecast
        List<DatabaseRecord> databaseRecordList = mainRepository.findTop2ByCountryNameOrderByDateDesc(countrySlug);
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        int magicIncrement = 40;
        int x = databaseRecordList.get(0).getRecovered()-databaseRecordList.get(1).getRecovered();
        while(to.compareTo(from) > 0){
            returnList.add(new CovidDailyCasesDto(from, x));
            from = addOneDay(from);
            x+=magicIncrement;
        }
        return returnList;

    }

    private List<CovidDailyCasesDto> getCountryDailyConfirmedForecast(Date from, Date to, String countrySlug){
        //TODO implement forecast
        List<DatabaseRecord> databaseRecordList = mainRepository.findTop2ByCountryNameOrderByDateDesc(countrySlug);
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        int magicIncrement = 3;
        int x = databaseRecordList.get(0).getRecovered()-databaseRecordList.get(1).getRecovered();
        while(to.compareTo(from) > 0){
            returnList.add(new CovidDailyCasesDto(from, x));
            from = addOneDay(from);
            x+=magicIncrement;
        }
        return returnList;

    }

}
