package estapar.mobilidade.mobilidadepassageiro;

import java.io.Serializable;

public abstract class InformationRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
