package com.reflex.response;

import java.util.List;

public class UnitTestResultResponse {
	
	private List<String> report;
	
	private boolean compileIsOk;
	
	public UnitTestResultResponse() {

	}
	
	public UnitTestResultResponse(List<String> report, boolean compileIsOk) {
		this.report = report;
		this.compileIsOk = compileIsOk;
	}

	public List<String> getReport() {
		return report;
	}

	public void setReport(List<String> report) {
		this.report = report;
	}

	public boolean compileIsOk() {
		return compileIsOk;
	}

	public void setCompileIsOk(boolean compileIsOk) {
		this.compileIsOk = compileIsOk;
	}

}
