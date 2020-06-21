package com.covis.Server.Services;


import com.covis.Server.DAO.MainRepository;
import com.covis.Server.DAO.PopulationRepository;
import com.covis.Server.Entities.CountryPopulationInfo;
import com.covis.Server.Entities.DatabaseRecord;
import com.covis.Server.Entities.SIRDModel;
import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
    public List<CovidDailyCasesDto> getDailyCases(Date fromDate, Date toDate, String countryCode, CovidCasesType type, Boolean isForecast){
        String countrySlug = populationRepository.findOneByCountrySlug(countryCode).get().getCountryName();
        LocalDate from = LocalDate.ofInstant(fromDate.toInstant(), ZoneId.of("Europe/Warsaw"));
        LocalDate to = LocalDate.ofInstant(toDate.toInstant(), ZoneId.of("Europe/Warsaw")).plusDays(1);
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

    private List<CovidDailyCasesDto> getCountryDailyDeaths(LocalDate from, LocalDate to, String countrySlug){
        LocalDate oneDayBeforeFrom = from.minusDays(1);
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetweenOrderByDateAsc(countrySlug, from, to)){
            if(tempRecord.isPresent()){
                returnList.add(new CovidDailyCasesDto(java.sql.Date.valueOf(databaseRecord.getDate()), databaseRecord.getDeaths() - tempRecord.get().getDeaths()));
            } else{
                returnList.add(new CovidDailyCasesDto(java.sql.Date.valueOf(databaseRecord.getDate()), 0));
            }
            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyConfirmed(LocalDate from, LocalDate to, String countrySlug){
        LocalDate oneDayBeforeFrom = from.minusDays(1);
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetweenOrderByDateAsc(countrySlug, from, to)){
            if(tempRecord.isPresent()){
                returnList.add(new CovidDailyCasesDto(java.sql.Date.valueOf(databaseRecord.getDate()), databaseRecord.getConfirmed() - tempRecord.get().getConfirmed()));
            } else{
                returnList.add(new CovidDailyCasesDto(java.sql.Date.valueOf(databaseRecord.getDate()), 0));
            }

            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyRecovered(LocalDate from, LocalDate to, String countrySlug){
        LocalDate oneDayBeforeFrom = from.minusDays(1);
        List<CovidDailyCasesDto> returnList = new ArrayList<>();
        Optional<DatabaseRecord> tempRecord = mainRepository.findOneByCountryNameAndDate(countrySlug, oneDayBeforeFrom);

        for(DatabaseRecord databaseRecord: mainRepository.findAllByCountryNameAndDateBetweenOrderByDateAsc(countrySlug, from, to)){
            if(tempRecord.isPresent()){
                returnList.add(new CovidDailyCasesDto(java.sql.Date.valueOf(databaseRecord.getDate()), databaseRecord.getRecovered() - tempRecord.get().getRecovered()));
            } else{
                returnList.add(new CovidDailyCasesDto(java.sql.Date.valueOf(databaseRecord.getDate()), 0));
            }
            tempRecord = Optional.of(databaseRecord);
        }
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyActive(LocalDate from, LocalDate to, String countrySlug){

        return mainRepository.findAllByCountryNameAndDateBetweenOrderByDateAsc(countrySlug,from,to).stream().map(
                elem -> new CovidDailyCasesDto(java.sql.Date.valueOf(elem.getDate()), elem.getConfirmed()-elem.getDeaths()-elem.getRecovered())
        ).collect(Collectors.toList());
    }

    private List<CovidDailyCasesDto> getCountryDailyActiveForecast(LocalDate from, LocalDate to, String countryName){
        //TODO implement forecast
        SIRDModel model = new SIRDModel(0.5, 0.0714, 0.053);
        Optional<DatabaseRecord> lastRecordOpt = mainRepository.findFirstByCountryNameOrderByDateDesc(countryName);
        DatabaseRecord lastRecord= lastRecordOpt.get();

        LocalDate dateAfterLast = lastRecord.getDate().plusDays(1);
        LocalDate toLocalDate = to;
        Optional<CountryPopulationInfo> population = populationRepository.findOneByCountryName(countryName);
        List<CovidDailyCasesDto> returnList = model.resolve(BigDecimal.valueOf(population.get().getPopulation()*0.8), BigDecimal.valueOf(lastRecord.getConfirmed()), BigDecimal.valueOf(lastRecord.getRecovered()), BigDecimal.valueOf(lastRecord.getDeaths()),dateAfterLast,from,toLocalDate, CovidCasesType.ACTIVE);
        return returnList;
    }

    private List<CovidDailyCasesDto> getCountryDailyRecoveredForecast(LocalDate from, LocalDate to, String countryName){
        //TODO implement forecast
        SIRDModel model = new SIRDModel(0.5, 0.0714, 0.053);
        Optional<DatabaseRecord> lastRecordOpt = mainRepository.findFirstByCountryNameOrderByDateDesc(countryName);
        DatabaseRecord lastRecord= lastRecordOpt.get();

        LocalDate dateAfterLast = lastRecord.getDate().plusDays(1);
        LocalDate toLocalDate = to;
        Optional<CountryPopulationInfo> population = populationRepository.findOneByCountryName(countryName);
        List<CovidDailyCasesDto> returnList = model.resolve(BigDecimal.valueOf(population.get().getPopulation()*0.8), BigDecimal.valueOf(lastRecord.getConfirmed()), BigDecimal.valueOf(lastRecord.getRecovered()), BigDecimal.valueOf(lastRecord.getDeaths()),dateAfterLast,from,toLocalDate, CovidCasesType.RECOVERED);
        return returnList;
    }

    public List<CovidDailyCasesDto> getCountryDailyDeathsForecast(LocalDate from, LocalDate to, String countryName){
        //TODO implement forecast
        SIRDModel model = new SIRDModel(0.5, 0.0714, 0.053);
        Optional<DatabaseRecord> lastRecordOpt = mainRepository.findFirstByCountryNameOrderByDateDesc(countryName);
        DatabaseRecord lastRecord= lastRecordOpt.get();

        LocalDate dateAfterLast = lastRecord.getDate().plusDays(1);
        LocalDate toLocalDate = to;
        Optional<CountryPopulationInfo> population = populationRepository.findOneByCountryName(countryName);
        List<CovidDailyCasesDto> returnList = model.resolve(BigDecimal.valueOf(population.get().getPopulation()*0.8), BigDecimal.valueOf(lastRecord.getConfirmed()), BigDecimal.valueOf(lastRecord.getRecovered()), BigDecimal.valueOf(lastRecord.getDeaths()),dateAfterLast,from,toLocalDate, CovidCasesType.RECOVERED);
        return returnList;

    }

    private List<CovidDailyCasesDto> getCountryDailyConfirmedForecast(LocalDate from, LocalDate to, String countryName){
        //TODO implement forecast
        SIRDModel model = new SIRDModel(0.5, 0.0714, 0.053);
        Optional<DatabaseRecord> lastRecordOpt = mainRepository.findFirstByCountryNameOrderByDateDesc(countryName);
        DatabaseRecord lastRecord= lastRecordOpt.get();
        LocalDate dateAfterLast = lastRecord.getDate().plusDays(1);
        LocalDate toLocalDate = to;
        Optional<CountryPopulationInfo> population = populationRepository.findOneByCountryName(countryName);
        List<CovidDailyCasesDto> returnList = model.resolve(BigDecimal.valueOf(population.get().getPopulation()*0.8), BigDecimal.valueOf(lastRecord.getConfirmed()), BigDecimal.valueOf(lastRecord.getRecovered()), BigDecimal.valueOf(lastRecord.getDeaths()),dateAfterLast,from,toLocalDate, CovidCasesType.NEW);
        return returnList;
    }

}
