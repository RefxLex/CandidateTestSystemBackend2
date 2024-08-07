package com.reflex.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="topics", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class Topic {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max=70)
	@Column(nullable=false)
	private String name;
		
	public Topic() {
		
	}
	
	public Topic(@NotBlank String name) {
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
	
	@Override
	public String toString() {
		return "Topic [id=" + id + ", name=" + name + "]";
	}

}
