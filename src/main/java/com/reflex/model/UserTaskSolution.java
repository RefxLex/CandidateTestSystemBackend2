package com.reflex.model;

import jakarta.persistence.*;

@Entity
@Table(name="user_task_solutions")
public class UserTaskSolution {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, columnDefinition="TEXT")
	private String code;

	public UserTaskSolution() {
		
	}

	public UserTaskSolution(String code) {

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
