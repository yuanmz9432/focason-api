package api.lemonico.exception;

import api.lemonico.enums.ResponseCode;

public class LemonicoAPIException extends RuntimeException {

	private static final long serialVersionUID = -2216088032747995508L;

	private ResponseCode responseCode = null;

	public LemonicoAPIException(ResponseCode responseCode) {
		super(responseCode.getLabel());
		this.responseCode = responseCode;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

}
