package com.reflex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import com.reflex.model.ExecModulePort;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;



public interface ExecModulePortRepository extends JpaRepository<ExecModulePort, Long> {
	
	void deleteByport(Long port);
}
