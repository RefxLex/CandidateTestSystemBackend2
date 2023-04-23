package com.reflex.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="tasks")
public class Task {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max=50)
	@Column(nullable=false, unique=true)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="topic_id", nullable=false)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public Topic topic;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="difficulty_id", nullable=false)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public TaskDifficulty taskDifficulty;
	
	@Column(nullable=false, columnDefinition="TEXT")
	private String description;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name="task_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Set<TaskTestInput> taskTestInput = new HashSet<>();
	
	@Column(nullable=false)
	private boolean deleted = false;

	public Task() {
		
	}
	
	public Task(@NotBlank String name, Topic topic, TaskDifficulty taskDifficulty, String description){
		
		this.name = name;
		this.topic = topic;
		this.taskDifficulty = taskDifficulty;
		this.description = description;
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

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<TaskTestInput> getTaskTestInput() {
		return taskTestInput;
	}

	public void setTaskTestInput(Set<TaskTestInput> taskTestInput) {
		this.taskTestInput = taskTestInput;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public TaskDifficulty getTaskDifficulty() {
		return taskDifficulty;
	}

	public void setTaskDifficulty(TaskDifficulty taskDifficulty) {
		this.taskDifficulty = taskDifficulty;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", topic=" + topic + ", taskDifficulty=" + taskDifficulty
				+ ", description=" + description + ", taskTestInput=" + taskTestInput + ", deleted=" + deleted + "]";
	}

}
