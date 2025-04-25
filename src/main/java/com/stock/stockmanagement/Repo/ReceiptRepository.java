package com.stock.stockmanagement.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.stockmanagement.Model.Receipt;
import java.time.LocalDate;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByBagCodeAndEntryDateBetween(String bagCode, LocalDate fromDate, LocalDate toDate);

}
