package com.stock.stockmanagement.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.stock.stockmanagement.Model.Issued;
import com.stock.stockmanagement.Model.Receipt;
import com.stock.stockmanagement.Model.StockEntry;
import com.stock.stockmanagement.Repo.IssuedRepository;
import com.stock.stockmanagement.Repo.ReceiptRepository;
import com.stock.stockmanagement.Repo.StockEntryRepository;

import jakarta.annotation.PostConstruct;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockEntryRepository stockEntryRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    @Autowired
    private IssuedRepository issuedRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Override
    public void saveStockEntry(StockEntry stockEntry) {
        LocalDate entryDate = stockEntry.getEntryDate() != null ? stockEntry.getEntryDate() : LocalDate.now();
        String entryMonthYear = entryDate.format(formatter);
        stockEntry.setMonthYear(entryMonthYear);
        stockEntry.setEntryDate(entryDate);

        YearMonth entryYM = YearMonth.from(entryDate);
        YearMonth currentYM = YearMonth.now();

        StockEntry latestEntry = null;

        YearMonth loopYM = entryYM;

        while (!loopYM.isAfter(currentYM)) {
            String loopMonthYear = loopYM.format(formatter);

            Optional<StockEntry> loopEntryOpt =
                stockEntryRepository.findTopByBagCodeAndMonthYearOrderByEntryDateDesc(stockEntry.getBagCode(), loopMonthYear);

            StockEntry loopEntry;

            if (loopEntryOpt.isPresent()) {
                loopEntry = loopEntryOpt.get();

                // Always set opening based on previous month's closing
                if (latestEntry != null) {
                    loopEntry.setOpening(latestEntry.getClosing());
                } else {
                    String prevMonth = loopYM.minusMonths(1).format(formatter);
                    StockEntry prevEntry = stockEntryRepository
                        .findTopByBagCodeAndMonthYearOrderByEntryDateDesc(stockEntry.getBagCode(), prevMonth)
                        .orElse(null);
                    loopEntry.setOpening(prevEntry != null ? prevEntry.getClosing() : 0);
                }

            } else {
                loopEntry = new StockEntry();
                loopEntry.setBagCode(stockEntry.getBagCode());
                loopEntry.setMonthYear(loopMonthYear);
                loopEntry.setEntryDate(LocalDate.of(loopYM.getYear(), loopYM.getMonth(), 1));
                loopEntry.setReceipt(0);
                loopEntry.setIssued(0);

                // Set opening as usual
                if (latestEntry != null) {
                    loopEntry.setOpening(latestEntry.getClosing());
                } else {
                    String prevMonth = loopYM.minusMonths(1).format(formatter);
                    StockEntry prevEntry = stockEntryRepository
                        .findTopByBagCodeAndMonthYearOrderByEntryDateDesc(stockEntry.getBagCode(), prevMonth)
                        .orElse(null);
                    loopEntry.setOpening(prevEntry != null ? prevEntry.getClosing() : 0);
                }
            }

            // If this is the month we're submitting data for
            if (loopYM.equals(entryYM)) {
                if ("receipt".equalsIgnoreCase(stockEntry.getType())) {
                    loopEntry.setReceipt(loopEntry.getReceipt() + stockEntry.getItemCount());
                    // Inside "receipt" condition
                    Receipt receipt = new Receipt();
                    receipt.setBagCode(stockEntry.getBagCode());
                    receipt.setEntryDate(entryDate);
                    receipt.setItemCount(stockEntry.getItemCount());
                    receipt.setCreatedAt(LocalDateTime.now());
                    receiptRepository.save(receipt);

                } else if ("issued".equalsIgnoreCase(stockEntry.getType())) {
                    int available = loopEntry.getOpening() + loopEntry.getReceipt() - loopEntry.getIssued();
                    if (stockEntry.getItemCount() > available) {
                        throw new IllegalArgumentException("Cannot issue more than available stock.");
                    }
                    Issued issued = new Issued();
                    issued.setBagCode(stockEntry.getBagCode());
                    issued.setEntryDate(entryDate);
                    issued.setItemCount(stockEntry.getItemCount());
                    issued.setCreatedAt(LocalDateTime.now());
                    issuedRepository.save(issued);
                    loopEntry.setIssued(loopEntry.getIssued() + stockEntry.getItemCount());
                } else {
                    throw new IllegalArgumentException("Invalid stock entry type.");
                }
            }

            loopEntry.setClosing(loopEntry.getOpening() + loopEntry.getReceipt() - loopEntry.getIssued());
            stockEntryRepository.save(loopEntry);

            latestEntry = loopEntry;
            loopYM = loopYM.plusMonths(1); // move to next month
        }
    }

    @Override
    public List<StockEntry> getAllStockEntries() {
        return stockEntryRepository.findAll();
    }

    @Override
    public List<String> getAllBagCodes() {
        return stockEntryRepository.findAllDistinctBagCodes();
    }

    @PostConstruct
    public void ensureCurrentMonthRowsExistOnStartup() {
        ensureCurrentMonthEntriesExist();
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void updateOpeningForNewMonth() {
        ensureCurrentMonthEntriesExist();
    }

    public void ensureCurrentMonthEntriesExist() {
        String currentMonthYear = LocalDate.now().format(formatter);
        String previousMonthYear = LocalDate.now().minusMonths(1).format(formatter);
        List<String> allBagCodes = stockEntryRepository.findAllDistinctBagCodes();

        for (String bagCode : allBagCodes) {
            Optional<StockEntry> currentEntry = stockEntryRepository.findTopByBagCodeAndMonthYearOrderByEntryDateDesc(bagCode, currentMonthYear);

            if (currentEntry.isEmpty()) {
                StockEntry previousEntry = stockEntryRepository.findTopByBagCodeAndMonthYearOrderByEntryDateDesc(bagCode, previousMonthYear).orElse(null);
                StockEntry newEntry = new StockEntry();
                newEntry.setBagCode(bagCode);
                newEntry.setMonthYear(currentMonthYear);
                newEntry.setOpening(previousEntry != null ? previousEntry.getClosing() : 0);
                newEntry.setReceipt(0);
                newEntry.setIssued(0);
                newEntry.setClosing(newEntry.getOpening());
                newEntry.setEntryDate(LocalDate.now());
                stockEntryRepository.save(newEntry);
            }
        }
    }
    public Object getEntriesByType(String type) {
        if ("receipt".equalsIgnoreCase(type)) {
            return receiptRepository.findAll();
        } else if ("issued".equalsIgnoreCase(type)) {
            return issuedRepository.findAll();
        } else {
            throw new IllegalArgumentException("Invalid type. Must be 'receipt' or 'issued'.");
        }
    }
}
