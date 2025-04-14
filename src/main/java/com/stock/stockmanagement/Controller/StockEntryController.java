package com.stock.stockmanagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stock.stockmanagement.Model.StockEntry;
import com.stock.stockmanagement.Repo.StockEntryRepository;
import com.stock.stockmanagement.Service.StockService;
import com.stock.stockmanagement.Service.StockServiceImpl;

@RestController
@RequestMapping("/api/stock")
public class StockEntryController {

    @Autowired
    private StockEntryRepository stockEntryRepository;
   
    @Autowired
    private StockService stockService;
    @Autowired
    private StockServiceImpl stockServiceImp;
    
    @PostMapping("/submit")
    public void submitEntry(@RequestBody StockEntry stockEntry) {
        stockService.saveStockEntry(stockEntry);
    }

    @GetMapping("/report")
    public List<StockEntry> getStockReport() {
        return stockEntryRepository.findAll();
    }
    @PostMapping("/entries")
    public ResponseEntity<String> addEntry(@RequestBody StockEntry stockEntry) {
        stockServiceImp.saveStockEntry(stockEntry);
        return ResponseEntity.ok("Entry added or updated.");
    }
}