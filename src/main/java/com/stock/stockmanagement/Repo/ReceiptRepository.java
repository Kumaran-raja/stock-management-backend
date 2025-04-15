package com.stock.stockmanagement.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.stockmanagement.Model.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {}
