package com.reflex.model;

import jakarta.persistence.*;

@Entity
@Table(name="topics")
public class Topic {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false, unique=true)
	private String topic;
	
	public Topic() {
		
	}
	
	public Topic(String topic) {

		this.topic = topic;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String toString() {
		return "Topic [id=" + id + ", topic=" + topic + "]";
	}
	
}
