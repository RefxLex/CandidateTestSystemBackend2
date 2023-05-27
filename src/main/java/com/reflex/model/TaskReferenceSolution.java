package com.reflex.model;

import jakarta.persistence.*;

@Entity
@Table(name="task_ref_solutions")
public class TaskReferenceSolution {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, columnDefinition="TEXT")
	private String code;

	public TaskReferenceSolution() {
		
	}

	public TaskReferenceSolution(String code) {

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
