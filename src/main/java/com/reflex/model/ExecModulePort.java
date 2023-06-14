package com.reflex.model;

import jakarta.persistence.*;

@Entity
@Table(name="exec_module_ports")
public class ExecModulePort {
		
	@Id
	private Long port;

	public ExecModulePort() {
		
	}

	public ExecModulePort(Long port) {

		this.port = port;
	}

	public long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}
	
}
