package com.stock.stockmanagement.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.stockmanagement.Model.Issued;

public interface IssuedRepository extends JpaRepository<Issued, Long> {}