package com.reflex.response;

public class LanguageResponse {
	
	private Long id;
	private String name;
	private boolean is_archived;
	
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
	public boolean isIs_archived() {
		return is_archived;
	}
	public void setIs_archived(boolean is_archived) {
		this.is_archived = is_archived;
	}

}
