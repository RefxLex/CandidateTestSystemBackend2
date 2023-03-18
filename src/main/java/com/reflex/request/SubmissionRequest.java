package com.reflex.request;


public class SubmissionRequest {
	
	private String source_code;
	
	private int language_id;
	
	private String compiler_options;
	
	private String command_line_arguments;
	
	private String stdin;
	
	private String expected_output;
	
	// must be base64 encoded
	private String additional_files;
	
	private String callback_url;

	public SubmissionRequest() {
		
	}
	public SubmissionRequest(String source_code, int language_id) {
		this.source_code = source_code;
		this.language_id = language_id;
	}
	
	public String getSource_code() {
		return source_code;
	}
	public void setSource_code(String source_code) {
		this.source_code = source_code;
	}
	public int getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(int language_id) {
		this.language_id = language_id;
	}
	public String getCompiler_options() {
		return compiler_options;
	}
	public void setCompiler_options(String compiler_options) {
		this.compiler_options = compiler_options;
	}
	public String getCommand_line_arguments() {
		return command_line_arguments;
	}
	public void setCommand_line_arguments(String command_line_arguments) {
		this.command_line_arguments = command_line_arguments;
	}
	public String getStdin() {
		return stdin;
	}
	public void setStdin(String stdin) {
		this.stdin = stdin;
	}
	public String getExpected_output() {
		return expected_output;
	}
	public void setExpected_output(String expected_output) {
		this.expected_output = expected_output;
	}
	public String getAdditional_files() {
		return additional_files;
	}
	public void setAdditional_files(String additional_files) {
		this.additional_files = additional_files;
	}
	public String getCallback_url() {
		return callback_url;
	}
	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}
}
