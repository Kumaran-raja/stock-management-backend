package com.stock.stockmanagement.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.stock.stockmanagement.Model.StockEntry;
import com.stock.stockmanagement.Repo.StockEntryRepository;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockEntryRepository stockEntryRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public void saveStockEntry(StockEntry stockEntry) {
        String currentMonthYear = LocalDate.now().format(formatter);
        stockEntry.setMonthYear(currentMonthYear);

        Optional<StockEntry> existingCurrentEntryOpt = stockEntryRepository.findTopByBagCodeAndMonthYearOrderByEntryDateDesc(
                stockEntry.getBagCode(), currentMonthYear);

        StockEntry entryToUpdate;

        if (existingCurrentEntryOpt.isPresent()) {
            entryToUpdate = existingCurrentEntryOpt.get();
        } else {
            StockEntry previousEntry = stockEntryRepository.findByBagCode(stockEntry.getBagCode());
            entryToUpdate = new StockEntry();
            entryToUpdate.setBagCode(stockEntry.getBagCode());
            entryToUpdate.setMonthYear(currentMonthYear);
            entryToUpdate.setOpening(previousEntry != null ? previousEntry.getClosing() : 0);
            entryToUpdate.setReceipt(0);
            entryToUpdate.setIssued(0);
            entryToUpdate.setClosing(entryToUpdate.getOpening());
        }

        if ("receipt".equalsIgnoreCase(stockEntry.getType())) {
            entryToUpdate.setReceipt(entryToUpdate.getReceipt() + stockEntry.getItemCount());
        } else if ("issued".equalsIgnoreCase(stockEntry.getType())) {
            int availableStock = entryToUpdate.getOpening() + entryToUpdate.getReceipt() - entryToUpdate.getIssued();
            if (stockEntry.getItemCount() > availableStock) {
                throw new IllegalArgumentException("Cannot issue more than available stock.");
            }
            entryToUpdate.setIssued(entryToUpdate.getIssued() + stockEntry.getItemCount());
        } else {
            throw new IllegalArgumentException("Invalid stock entry type.");
        }

        entryToUpdate.setClosing(entryToUpdate.getOpening() + entryToUpdate.getReceipt() - entryToUpdate.getIssued());
        stockEntryRepository.save(entryToUpdate);
    }

    @Override
    public List<StockEntry> getAllStockEntries() {
        return stockEntryRepository.findAll();
    }

    @Override
    public List<String> getAllBagCodes() {
        return stockEntryRepository.findAllDistinctBagCodes();
    }

    @Scheduled(cron = "0 0 1 1 * ?") // Every 1st of the month at 1:00 AM
    public void updateOpeningForNewMonth() {
        List<StockEntry> allEntries = stockEntryRepository.findAll();
        for (StockEntry entry : allEntries) {
            entry.setOpening(entry.getClosing());
            entry.setReceipt(0);
            entry.setIssued(0);
            stockEntryRepository.save(entry);
        }
    }
} 