package com.covis.Server.Entities;

import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SIRDModel {

    private BigDecimal beta;
    private BigDecimal gamma;
    private BigDecimal mi; //death rate

    //initial S 80% of population
    public List<CovidDailyCasesDto> resolve (BigDecimal initialS, BigDecimal initialI, BigDecimal initialR, BigDecimal initialD, LocalDate initialDate, LocalDate fromDate,LocalDate lastDate, CovidCasesType type){
        List<CovidDailyCasesDto> returnValue = new ArrayList<>();
        BigDecimal N = initialS;
        BigDecimal todayS = initialS;
        BigDecimal todayI = initialI;
        BigDecimal todayR = initialR;
        BigDecimal todayD = initialD;
        LocalDate dateTemp = initialDate;
        Integer dailyActive, dailyInfected, dailyRecovered, dailyDeaths, toPut;
        toPut = -1;
        BigDecimal tomorrowS, tomorrowI, tomorrowR, tomorrowD;

        while(!dateTemp.minusDays(1).equals(lastDate)){

            tomorrowS = todayS.subtract(beta.multiply(todayS.multiply(todayI, MathContext.DECIMAL32),MathContext.DECIMAL32).divide(N, MathContext.DECIMAL32));
            tomorrowI = todayI.add(beta.multiply(todayS.multiply(todayI,MathContext.DECIMAL32),MathContext.DECIMAL32).divide(N, MathContext.DECIMAL32)).subtract(gamma.multiply(todayI, MathContext.DECIMAL32)).subtract(mi.multiply(todayI,MathContext.DECIMAL32));
            tomorrowR = todayR.add(gamma.multiply(todayI, MathContext.DECIMAL32));
            tomorrowD = todayD.add(mi.multiply(todayI, MathContext.DECIMAL32));
            dailyActive = tomorrowI.intValue();
            dailyInfected = beta.multiply(tomorrowS.multiply(tomorrowI, MathContext.DECIMAL32),MathContext.DECIMAL32).divide(N, MathContext.DECIMAL32).intValue();
            dailyRecovered = gamma.multiply(tomorrowI,MathContext.DECIMAL32).intValue();
            dailyDeaths = mi.multiply(tomorrowI,MathContext.DECIMAL32).intValue();
            switch (type){
                case NEW:
                    toPut = dailyInfected;
                    break;
                case DEATH:
                    toPut = dailyDeaths;
                    break;
                case ACTIVE:
                    toPut = dailyActive;
                    break;
                case RECOVERED:
                    toPut = dailyRecovered;
                    break;
            }
            if(dateTemp.isEqual(fromDate) || dateTemp.isAfter(fromDate)){
                returnValue.add(new CovidDailyCasesDto(java.sql.Date.valueOf(dateTemp), toPut));
            }
            dateTemp = dateTemp.plusDays(1);
            todayS = tomorrowS;
            todayI = tomorrowI;
            todayR = tomorrowR;
            todayD = tomorrowD;
        }
        return returnValue;
    }
    //beta-infection rate  gamma-recovery rate mi-death rate
    public SIRDModel(BigDecimal beta, BigDecimal gamma, BigDecimal mi) {
        this.beta = beta;
        this.gamma = gamma;
        this.mi = mi;
    }

    public static List<BigDecimal> calculateModelVariables(List<DatabaseRecord> data){
        List<BigDecimal> returnValue = new ArrayList<>();
        BigDecimal sumOfBeta = BigDecimal.ZERO;
        BigDecimal sumOfGamma = BigDecimal.ZERO;
        BigDecimal sumOfMi = BigDecimal.ZERO;
        int divide = 0;
        Integer deltaC;
        Integer deltaR;
        Integer deltaD;
        Integer I;
        boolean isFirst = true;
        DatabaseRecord yesterday = null;
        for(DatabaseRecord today: data){
            if(isFirst){
                isFirst = false;
                yesterday = today;
                continue;
            }
            I = today.getConfirmed() - today.getRecovered() - today.getDeaths();
            deltaR = today.getRecovered() - yesterday.getRecovered();
            deltaD = today.getDeaths() - yesterday.getDeaths();
            deltaC = today.getConfirmed() - yesterday.getConfirmed();
            sumOfBeta = sumOfBeta.add(BigDecimal.valueOf(deltaC).divide(BigDecimal.valueOf(I),MathContext.DECIMAL32));
            sumOfGamma = sumOfGamma.add(BigDecimal.valueOf(deltaR).divide(BigDecimal.valueOf(I),MathContext.DECIMAL32));
            sumOfMi = sumOfMi.add(BigDecimal.valueOf(deltaD).divide(BigDecimal.valueOf(I),MathContext.DECIMAL32));
            divide++;
        }
        returnValue.add(sumOfBeta.divide(BigDecimal.valueOf(divide),MathContext.DECIMAL32));
        returnValue.add(sumOfGamma.divide(BigDecimal.valueOf(divide),MathContext.DECIMAL32));
        returnValue.add(sumOfMi.divide(BigDecimal.valueOf(divide),MathContext.DECIMAL32));
        return returnValue;
    }

    public static List<DatabaseRecord> fixValues(List<DatabaseRecord> data){
        Integer confirmed = null;
        Integer recovered = null;
        Integer death = null;
        DatabaseRecord temp;
        Optional<Integer> confirmedNullable;
        Optional<Integer> recoveredNullable;
        Optional<Integer> deathNullable;
        boolean isNeededBackwards = false;
        for(DatabaseRecord record: data){
            confirmedNullable = Optional.ofNullable(record.getConfirmed());
            recoveredNullable = Optional.ofNullable(record.getRecovered());
            deathNullable = Optional.ofNullable(record.getDeaths());
            if(confirmedNullable.isPresent()){
                confirmed = confirmedNullable.get();
            } else {
                if(Optional.ofNullable(confirmed).isPresent()){
                    record.setConfirmed(confirmed);
                }else {
                    isNeededBackwards = true;
                }
            }
            if(recoveredNullable.isPresent()){
                recovered = recoveredNullable.get();
            } else {
                if(Optional.ofNullable(recovered).isPresent()){
                    record.setRecovered(recovered);
                }else {
                    isNeededBackwards = true;
                }
            }
            if(deathNullable.isPresent()){
                death = deathNullable.get();
            } else {
                if(Optional.ofNullable(death).isPresent()){
                    record.setDeaths(death);
                }else {
                    isNeededBackwards = true;
                }
            }

        }
        if(isNeededBackwards){
            for (int i = data.size(); i-- > 0; ) {
                temp = data.get(i);
                confirmedNullable = Optional.ofNullable(temp.getConfirmed());
                recoveredNullable = Optional.ofNullable(temp.getRecovered());
                deathNullable = Optional.ofNullable(temp.getDeaths());
                if(confirmedNullable.isPresent()){
                    confirmed = confirmedNullable.get();
                } else {
                    temp.setConfirmed(confirmed);
                }
                if(recoveredNullable.isPresent()){
                    recovered = recoveredNullable.get();
                } else {
                    temp.setRecovered(recovered);
                }
                if(deathNullable.isPresent()){
                    death = deathNullable.get();
                } else {
                    temp.setDeaths(death);
                }
            }
        }
        return data;
    }
}
