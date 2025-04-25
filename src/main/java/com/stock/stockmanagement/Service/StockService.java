package com.stock.stockmanagement.Service;

import java.time.LocalDate;
import java.util.List;
import com.stock.stockmanagement.Model.StockEntry;


public interface StockService {
    void saveStockEntry(StockEntry stockEntry);
    List<StockEntry> getAllStockEntries();
    List<String> getAllBagCodes();
    Object getEntriesByType(String type);
    Object getStockEntriesBetweenDates(LocalDate fromDate, LocalDate toDate);
}
