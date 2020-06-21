package com.covis.Server.Entities;

import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SIRDModel {

    private Double beta;
    private Double gamma;
    private Double mi; //death rate

    //initial S 80% of population
    public List<CovidDailyCasesDto> resolve (BigDecimal initialS, BigDecimal initialI, BigDecimal initialR, BigDecimal initialD, LocalDate initialDate, LocalDate fromDate,LocalDate lastDate, CovidCasesType type){
        List<CovidDailyCasesDto> returnValue = new ArrayList<>();
        BigDecimal N = initialS.add(initialI).add(initialR).add(initialD);
        BigDecimal todayS = initialS;
        BigDecimal todayI = initialI;
        BigDecimal todayR = initialR;
        BigDecimal todayD = initialD;
        LocalDate dateTemp = initialDate;
        Integer dailyActive, dailyInfected, dailyRecovered, dailyDeaths, toPut;
        toPut = -1;
        BigDecimal tomorrowS, tomorrowI, tomorrowR, tomorrowD;

        while(!dateTemp.minusDays(1).equals(lastDate)){

            tomorrowS = todayS.subtract(BigDecimal.valueOf(beta).multiply(todayS.multiply(todayI).setScale(4,RoundingMode.CEILING)).divide(N, RoundingMode.HALF_DOWN));
            tomorrowI = todayI.add(BigDecimal.valueOf(beta).multiply(todayS.multiply(todayI)).setScale(4,RoundingMode.CEILING).divide(N, RoundingMode.HALF_DOWN)).subtract(BigDecimal.valueOf(gamma).multiply(todayI)).subtract(BigDecimal.valueOf(mi).multiply(todayI));
            tomorrowR = todayR.add(BigDecimal.valueOf(gamma).multiply(todayI));
            tomorrowD = todayD.add(BigDecimal.valueOf(mi).multiply(todayI));
            dailyActive = tomorrowI.intValue();
            dailyInfected = BigDecimal.valueOf(beta).multiply(todayS.multiply(todayI)).setScale(4,RoundingMode.CEILING).divide(N, RoundingMode.HALF_DOWN).intValue();
            dailyRecovered = BigDecimal.valueOf(gamma).multiply(todayI).intValue();
            dailyDeaths = BigDecimal.valueOf(mi).multiply(todayI).intValue();
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
            todayS = tomorrowS.setScale(4, RoundingMode.CEILING);
            todayI = tomorrowI.setScale(4,RoundingMode.CEILING);
            todayR = tomorrowR.setScale(4,RoundingMode.CEILING);;
            todayD = tomorrowD.setScale(4,RoundingMode.CEILING);;
        }
        return returnValue;
    }
    //beta-infection rate  gamma-recovery rate mi-death rate
    public SIRDModel(Double beta, Double gamma, Double mi) {
        this.beta = beta;
        this.gamma = gamma;
        this.mi = mi;
    }
}
