package com.reflex.model;

import java.time.Instant;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.reflex.model.enums.TaskStatus;

import jakarta.persistence.*;

@Entity
@Table(name="user_tasks")
public class UserTasks {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private User user;
	
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private Task task;
	
	@Column(name="task_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private TaskStatus taskStatus;
	
	@Column(name="assing_date", nullable=false)
	private Instant assignDate;
	
	@Column(nullable=true)
	private String result;
	
	@Column(nullable=true)
	private String code;
	
	@Column(name="start_date", nullable=true)
	private Instant startDate;
	
	@Column(name="submit_date", nullable=true)
	private Instant submitDate;
	
	@Column(nullable=true)
	private String comment;
	
	
	
}
