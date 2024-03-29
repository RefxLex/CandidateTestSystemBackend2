package com.reflex.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="task_difficulties" , uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class TaskDifficulty {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max=30)
	@Column(nullable = false)
	private String name;
	
	@Column(nullable=false)
	private boolean deleted = false;

	public TaskDifficulty() {
		
	}

	public TaskDifficulty(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
