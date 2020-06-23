package com.covis.Server.DAO;

import com.covis.Server.Entities.DatabaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MainRepository extends CrudRepository<DatabaseRecord, Integer>, JpaRepository<DatabaseRecord, Integer> {

    List<DatabaseRecord> findAllByCountryNameAndDateBetweenOrderByDateAsc(String countryName, LocalDate from, LocalDate to);

    Optional<DatabaseRecord> findFirstByCountryNameAndDate(String countryName, LocalDate date);
    Optional<DatabaseRecord> findOneByCountryNameAndProvinceAndDate(String countryName, String province, LocalDate date);
    List<DatabaseRecord> findAllByCountryNameAndDateBefore(String countryName, LocalDate date);
    List<DatabaseRecord> findAllByCountryNameAndDateAfterOrderByDateAsc(String countryName, LocalDate date);

    List<DatabaseRecord> findAllByDate(LocalDate date);

    List<DatabaseRecord> findTop2ByCountryNameOrderByDateDesc(String countryName);

    Optional<DatabaseRecord> findFirstByCountryNameOrderByDateDesc(String countryName);



}
