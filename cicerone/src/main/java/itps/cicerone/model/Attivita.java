package itps.cicerone.model;

import java.sql.Time;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.annotation.Timed;

public class Attivita {
	
	@Size(min=1, message="Nome deve essere inserito")
	@Pattern(regexp = "^[a-zA-Z]*$")
	private String nome;
	
	@Size(max=50, message="Massimo 50 caratteri")
	private String descrizione;
	
	@Size(min=1, message="Città deve essere inserita")
	@Pattern(regexp = "^[a-zA-Z]*$")
	private String citta;
	
	@Size(min=1, max=2, message="Provincia deve essere inserita")
	@Pattern(regexp = "^[a-zA-Z]*$")
	private String prov;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date data;
	
	@NotNull
	private Time orario;
	
	@Size(min=1, message="Prezzo deve essere inserito")
	@Pattern(regexp="[0-9]+([,.][0-9]{1,2})?")
	private String prezzo;
	
    @Min(1)
	private int maxPartecipanti;
	
	private int idAttivita;
	
	private int idCicerone;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Time getOrario() {
		return orario;
	}

	public void setOrario(Time orario) {
		this.orario = orario;
	}

	public String getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(String prezzo) {
		this.prezzo = prezzo;
	}

	public int getMaxPartecipanti() {
		return maxPartecipanti;
	}

	public void setMaxPartecipanti(int maxPartecipanti) {
		this.maxPartecipanti = maxPartecipanti;
	}

	public int getIdAttivita() {
		return idAttivita;
	}

	public void setIdAttivita(int idAttivita) {
		this.idAttivita = idAttivita;
	}

	public int getIdCicerone() {
		return idCicerone;
	}

	public void setIdCicerone(int idCicerone) {
		this.idCicerone = idCicerone;
	}

}
