package com.example.joboui.utils;

import com.fasterxml.jackson.annotation.JsonInclude;


public class JsonResponse {
	private boolean success;
	private boolean has_error;
	private int api_code;
	private String api_code_description;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String trx_id;

	private Object data;


	public JsonResponse() {
		super();
	}

	public JsonResponse(boolean success, boolean has_error, int api_code, String api_code_description,
                        String message, String trx_id, Object data) {
		super();
		this.success = success;
		this.has_error = has_error;
		this.api_code = api_code;
		this.api_code_description = api_code_description;
		this.message = message;
		this.trx_id = trx_id;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isHas_error() {
		return has_error;
	}

	public void setHas_error(boolean has_error) {
		this.has_error = has_error;
	}

	public int getApi_code() {
		return api_code;
	}

	public void setApi_code(int api_code) {
		this.api_code = api_code;
	}

	public String getApi_code_description() {
		return api_code_description;
	}

	public void setApi_code_description(String api_code_description) {
		this.api_code_description = api_code_description;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTrx_id() {
		return trx_id;
	}

	public void setTrx_id(String trx_id) {
		this.trx_id = trx_id;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
