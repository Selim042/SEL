package us.myles_selim.sel;

public class SELException extends Exception {

	private String error;

	public SELException(String error) {
		this.error = error;
	}

	public String getEsotericError() {
		return error;
	}

	private static final long serialVersionUID = 8547922269741060186L;

}
