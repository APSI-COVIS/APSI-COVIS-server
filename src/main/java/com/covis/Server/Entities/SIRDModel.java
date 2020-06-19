package com.covis.Server.Entities;

import com.covis.api.country.dto.CovidDailyCasesDto;
import com.covis.api.covid.CovidCasesType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SIRDModel {

    private Double beta;
    private Double gamma;
    private Double mi; //death rate

    //initial S 80% of population
    public List<CovidDailyCasesDto> resolve (Double initialS, Double initialI, Double initialR, Double initialD, LocalDate initialDate, LocalDate lastDate, CovidCasesType type){
        List<CovidDailyCasesDto> returnValue = new ArrayList<>();

        Double todayS = initialS;
        Double todayI = initialI;
        Double todayR = initialR;
        Double todayD = initialD;
        LocalDate dateTemp = initialDate;
        Integer dailyActive, dailyInfected, dailyRecovered, dailyDeaths, toPut;
        toPut = -1;
        Double tomorrowS, tomorrowI, tomorrowR, tomorrowD;
        while(!dateTemp.equals(lastDate)){
            tomorrowS = todayS - beta*todayS*todayI;
            tomorrowI = todayI + beta*todayS*todayI - gamma*todayI - mi*todayI;
            tomorrowR = todayR + gamma*todayI;
            tomorrowD = todayD + mi*todayI;
            dailyActive = Math.toIntExact(Math.round(tomorrowI));
            dailyInfected = Math.toIntExact(Math.round(tomorrowI - todayI));
            dailyRecovered = Math.toIntExact(Math.round(tomorrowR - todayR));
            dailyDeaths = Math.toIntExact(Math.round(tomorrowD - todayD));
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
            returnValue.add(new CovidDailyCasesDto(java.sql.Date.valueOf(dateTemp), toPut));
            dateTemp = dateTemp.plusDays(1);
            todayS = tomorrowS;
            todayI = tomorrowI;
            todayR = tomorrowR;
            todayD = tomorrowD;
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
