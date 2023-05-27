package com.reflex.model;

import jakarta.persistence.*;

@Entity
@Table(name="task_unit_tests")
public class TaskUnitTest {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, columnDefinition="TEXT")
	private String code;

	public TaskUnitTest() {
		
	}

	public TaskUnitTest(String code) {

		this.code = code;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
