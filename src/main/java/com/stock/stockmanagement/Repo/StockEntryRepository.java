package com.stock.stockmanagement.Repo;
import com.stock.stockmanagement.Model.StockEntry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
    StockEntry findByBagCode(String bagCode);

   @Query("SELECT s FROM StockEntry s WHERE s.bagCode = :bagCode AND MONTH(s.entryDate) = :month AND YEAR(s.entryDate) = :year ORDER BY s.entryDate DESC")
   Optional<StockEntry> findTopByBagCodeAndEntryDateMonth(@Param("bagCode") String bagCode,
                                                           @Param("month") int month,
                                                           @Param("year") int year);

   @Query("SELECT DISTINCT s.bagCode FROM StockEntry s")
    List<String> findAllDistinctBagCodes();

    @Query("SELECT s FROM StockEntry s WHERE s.bagCode = :bagCode AND s.monthYear = :monthYear ORDER BY s.entryDate DESC")
    Optional<StockEntry> findTopByBagCodeAndMonthYearOrderByEntryDateDesc(
        @Param("bagCode") String bagCode,
        @Param("monthYear") String monthYear
    );
    
}
