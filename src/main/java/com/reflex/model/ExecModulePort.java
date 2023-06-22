package com.reflex.model;

import jakarta.persistence.*;

@Entity
@Table(name="exec_module_ports")
public class ExecModulePort {
		
	@Id
	private Long port;
	
	@Column(nullable = false)
	private boolean active;

	public ExecModulePort() {
		
	}

	public ExecModulePort(Long port, boolean active) {
		this.active = active;
		this.port = port;
	}

	public long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
