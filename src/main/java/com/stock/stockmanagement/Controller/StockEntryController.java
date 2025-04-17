package com.stock.stockmanagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stock.stockmanagement.Model.StockEntry;
import com.stock.stockmanagement.Repo.StockEntryRepository;
import com.stock.stockmanagement.Service.StockService;
import com.stock.stockmanagement.Service.StockServiceImpl;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "https://stock-management-frontend-xmym.onrender.com")
public class StockEntryController {

    //  @Autowired used to create object for each class
    @Autowired
    private StockEntryRepository stockEntryRepository;
   
    @Autowired
    private StockService stockService;
    @Autowired
    private StockServiceImpl stockServiceImp;
    // it will handle the /api/stock/submit path api call
    @PostMapping("/submit")
    @CrossOrigin(origins = "https://stock-management-frontend-xmym.onrender.com")
    public void submitEntry(@RequestBody StockEntry stockEntry) {
        stockService.saveStockEntry(stockEntry);
    }

    @GetMapping("/report")
    @CrossOrigin(origins = "https://stock-management-frontend-xmym.onrender.com")
    public List<StockEntry> getStockReport() {
        return stockEntryRepository.findAll();
    }
    @PostMapping("/entries")
    @CrossOrigin(origins = "https://stock-management-frontend-xmym.onrender.com")
    public ResponseEntity<String> addEntry(@RequestBody StockEntry stockEntry) {
        stockServiceImp.saveStockEntry(stockEntry);
        return ResponseEntity.ok("Entry added or updated.");
    }
    @GetMapping("/entries")
    @CrossOrigin(origins = "https://stock-management-frontend-xmym.onrender.com")
    public ResponseEntity<?> getEntriesByType(@RequestParam String type) {
        try {
            Object result = stockService.getEntriesByType(type);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok("Catalyst API is running fine!");
    }
}