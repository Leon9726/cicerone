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

public class AttivitaRicercate {
	
	private int idPrenotazione;
	
	private int idAttivitaSalvate;
	
	private int partecipantiPrenotati;
	
	private String nome;
	
	private String descrizione;
	
	private String citta;
	
	private String prov;
	
	private Date data;
	
	private Time orario;
	
	private String prezzo;
	
	private int maxPartecipanti;
	
	private int idAttivita;
	
	private int idCicerone;
	
	private String nomeCicerone;
	
	private String cognomeCicerone;
	
	private String email;
	
	private int postiDisponibili;
	
	private int postiPrenotati;
	
	private String stato;

	public String getNomeCicerone() {
		return nomeCicerone;
	}

	public void setNomeCicerone(String nomeCicerone) {
		this.nomeCicerone = nomeCicerone;
	}

	public String getCognomeCicerone() {
		return cognomeCicerone;
	}

	public void setCognomeCicerone(String cognomeCicerone) {
		this.cognomeCicerone = cognomeCicerone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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

	public int getIdPrenotazione() {
		return idPrenotazione;
	}

	public void setIdPrenotazione(int idPrenotazione) {
		this.idPrenotazione = idPrenotazione;
	}

	public int getPartecipantiPrenotati() {
		return partecipantiPrenotati;
	}

	public void setPartecipantiPrenotati(int partecipantiPrenotati) {
		this.partecipantiPrenotati = partecipantiPrenotati;
	}

	public int getPostiDisponibili() {
		return postiDisponibili;
	}

	public void setPostiDisponibili(int postiDisponibili) {
		this.postiDisponibili = postiDisponibili;
	}

	public int getPostiPrenotati() {
		return postiPrenotati;
	}

	public void setPostiPrenotati(int postiPrenotati) {
		this.postiPrenotati = postiPrenotati;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public int getIdAttivitaSalvate() {
		return idAttivitaSalvate;
	}

	public void setIdAttivitaSalvate(int idAttivitaSalvate) {
		this.idAttivitaSalvate = idAttivitaSalvate;
	}

}
