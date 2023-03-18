package com.reflex.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name="tasks_test_input")
public class TaskTestInput {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(nullable=false, columnDefinition="TEXT")
	private String input;
	
	@NotBlank
	@Column(nullable=false, columnDefinition="TEXT")
	private String output;
	
	public TaskTestInput() {
		
	}

	public TaskTestInput(Long id, @NotBlank String input, @NotBlank String output) {
		this.id = id;
		this.input = input;
		this.output = output;	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
}
