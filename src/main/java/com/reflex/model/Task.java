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
@Table(name="tasks", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "language_id" }) })
public class Task {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max=50)
	@Column(nullable=false)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="topic_id", nullable=true)
	@OnDelete(action=OnDeleteAction.NO_ACTION)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public Topic topic;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="difficulty_id", nullable=false)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public TaskDifficulty taskDifficulty;
	
	@Column(nullable=false, columnDefinition="TEXT")
	private String description;
	
	@Column(name="language_id", nullable=false)
	private int taskCodeLanguageId;
	
	@Column(name="language_name", nullable=false)
	private String languageName;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name="task_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Set<TaskReferenceSolution> refSolution = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name="task_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Set<TaskUnitTest> unitTest = new HashSet<>();
	
	@Column(nullable=false)
	private boolean deleted = false;

	public Task() {
		
	}
	
	public Task(String name, Topic topic, TaskDifficulty taskDifficulty, String description, int taskCodeLanguageId, String languageName){
		
		this.name = name;
		this.topic = topic;
		this.taskDifficulty = taskDifficulty;
		this.description = description;
		this.taskCodeLanguageId = taskCodeLanguageId;
		this.languageName = languageName;
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
	
	public int getTaskCodeLanguageId() {
		return taskCodeLanguageId;
	}

	public void setTaskCodeLanguageId(int taskCodeLanguageId) {
		this.taskCodeLanguageId = taskCodeLanguageId;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public Set<TaskReferenceSolution> getRefSolution() {
		return refSolution;
	}

	public void setRefSolution(Set<TaskReferenceSolution> refSolution) {
		this.refSolution = refSolution;
	}

	public Set<TaskUnitTest> getUnitTest() {
		return unitTest;
	}

	public void setUnitTest(Set<TaskUnitTest> unitTest) {
		this.unitTest = unitTest;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", topic=" + topic + ", taskDifficulty=" + taskDifficulty
				+ ", description=" + description + ", deleted=" + deleted + "]";
	}

}
