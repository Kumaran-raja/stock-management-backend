package com.stock.stockmanagement.Service;

import java.util.List;
import com.stock.stockmanagement.Model.StockEntry;


public interface StockService {
    void saveStockEntry(StockEntry stockEntry);
    List<StockEntry> getAllStockEntries();
    List<String> getAllBagCodes();
}
