package com.stock.stockmanagement.Model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_entries")
public class StockEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bag_code", nullable = false)
    private String bagCode;

    @Column(name = "opening")
    private int opening;

    @Column(name = "receipt")
    private int receipt;

    @Column(name = "issued")
    private int issued;

    @Column(name = "closing")
    private int closing;

    @Column(name = "entry_date")  
    private LocalDate entryDate = LocalDate.now(); 

    @Column(name = "month_year")
    private String monthYear;


    @Transient
    private String type; 

    @Transient
    private int itemCount; 

    public String getMonthYear() {
        return monthYear;
    }
    
    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBagCode() { return bagCode; }
    public void setBagCode(String bagCode) { this.bagCode = bagCode; }

    public int getOpening() { return opening; }
    public void setOpening(int opening) { this.opening = opening; }

    public int getReceipt() { return receipt; }
    public void setReceipt(int receipt) { this.receipt = receipt; }

    public int getIssued() { return issued; }
    public void setIssued(int issued) { this.issued = issued; }

    public int getClosing() { return closing; }
    public void setClosing(int closing) { this.closing = closing; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    @PrePersist
    public void prePersist() {
        if (entryDate != null && (monthYear == null || monthYear.isEmpty())) {
            this.monthYear = entryDate.getYear() + "-" + String.format("%02d", entryDate.getMonthValue());
    }
}
}
