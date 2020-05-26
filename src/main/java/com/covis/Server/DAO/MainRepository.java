package com.covis.Server.DAO;

import com.covis.Server.Entities.DatabaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MainRepository extends CrudRepository<DatabaseRecord, Integer>, JpaRepository<DatabaseRecord, Integer> {

    List<DatabaseRecord> findAllByCountryNameAndDateBetween(String countryName, Date from, Date to);

    Optional<DatabaseRecord> findOneByCountryNameAndDate(String countryName, Date date);

    List<DatabaseRecord> findAllByCountryNameAndDateBefore(String countryName, Date date);

    List<DatabaseRecord> findAllByDate(Date date);

    List<DatabaseRecord> findTop2ByCountryNameOrderByDateDesc(String countryName);



}
