package com.reflex.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateCommentRequest {
	
	@NotBlank
	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
