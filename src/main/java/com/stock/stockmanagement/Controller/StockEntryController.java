package com.stock.stockmanagement.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.stock.stockmanagement.Model.StockEntry;
import com.stock.stockmanagement.Service.StockService;
import com.stock.stockmanagement.Service.StockServiceImpl;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
@RestController
@RequestMapping("/api/stock")
public class StockEntryController {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockServiceImpl stockServiceImp;

    @PostMapping("/submit")
    public void submitEntry(@ModelAttribute StockEntry stockEntry) {
        stockService.saveStockEntry(stockEntry);
    }
    @GetMapping("/report")
    public ResponseEntity<Object> getReport(
        @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(stockService.getStockEntriesBetweenDates(fromDate, toDate));
    }

    @PostMapping("/entries")
    public ResponseEntity<String> addEntry(@RequestBody StockEntry stockEntry) {
        stockServiceImp.saveStockEntry(stockEntry);
        return ResponseEntity.ok("Entry added or updated.");
    }
    @GetMapping("/entries")
    public ResponseEntity<?> getEntriesByType(@RequestParam String type) {
        try {
            Object result = stockService.getEntriesByType(type);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the error to console
            e.printStackTrace(); // for debugging
            return ResponseEntity.internalServerError().body("Server error: " + e.getMessage());
        }
    }
}