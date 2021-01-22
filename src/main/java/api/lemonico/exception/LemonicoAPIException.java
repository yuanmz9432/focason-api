package api.lemonico.exception;

public class LemonicoAPIException extends RuntimeException {

	private static final long serialVersionUID = -2216088032747995508L;

	String params = null;

	public LemonicoAPIException(String message) {

		super(message);
	}

	public LemonicoAPIException(String message, String params) {

		super(message);
		this.params = params;
	}

	public String getParams() {
		return params;
	}

}
