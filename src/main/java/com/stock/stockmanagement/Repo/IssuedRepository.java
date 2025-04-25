package com.stock.stockmanagement.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.stockmanagement.Model.Issued;
import java.time.LocalDate;
import java.util.List;

public interface IssuedRepository extends JpaRepository<Issued, Long> {
    List<Issued> findByBagCodeAndEntryDateBetween(String bagCode, LocalDate fromDate, LocalDate toDate);

}