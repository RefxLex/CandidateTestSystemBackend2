package com.reflex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.reflex.model.ExecModulePort;

@Repository
public interface ExecModulePortRepository extends JpaRepository<ExecModulePort, Long> {
	
	@Query(
			value = "SELECT * FROM exec_module_ports WHERE active = false",
			nativeQuery = true)
	List<ExecModulePort> selectNotActive();
	
	Optional<ExecModulePort> findByport(Long port);
	//void deleteByport(Long port);
}
