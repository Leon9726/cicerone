package itps.cicerone.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class Utente {
	
	private int idUtente;
	
	@Size(min=1, message="Nome deve essere inserito")
	private String nome;
	
	@Size(min=1, message="Cognome deve essere inserito")
	private String cognome;

	@Size(min=1, message="Email deve essere inserito")
	@Email
	private String email;
	
	@Size(min=1, message="Password deve essere inserito")
	private String password;
	
	@Size(min=1, max=10, message="Cellulare deve essere inserito")
	private String cellulare;
	
	private int stato;

	public int getStato() {
		return stato;
	}

	public void setStato(int stato) {
		this.stato = stato;
	}

	public int getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(int idUtente) {
		this.idUtente = idUtente;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}
}
