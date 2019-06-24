package itps.cicerone.persistance;

import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import itps.cicerone.model.Attivita;
import itps.cicerone.model.AttivitaMapper;
import itps.cicerone.model.Utente;
import itps.cicerone.model.UtenteMapper;

@Component
public class DbQuery {

	@Autowired
	JdbcTemplate jdbc;

	private static final String SELECT_UTENTE_BY_EMAIL = "select * from utenti where Email=?";
	
	private static final String DISATTIVA_UTENTE = "UPDATE utenti SET stato = 0 WHERE (id_utente = ?)";
	
	private static final String SELECT_ID_UTENTE_BY_EMAIL = "select id_utente from utenti where Email=?";

	private static final String INSERT_UTENTE = "INSERT INTO utenti (Nome, Cognome, Email, Password, Cellulare) VALUES (?, ?, ?, ?, ?)";

	private static final String UPDATE_UTENTE = "UPDATE utenti SET Nome = ?, Cognome = ?, Email = ?, Password = ?, Cellulare = ? WHERE (id_utente = ?)";
	
	private static final String SELECT_ATTIVITA = "select * from attivita where id_cicerone=?";
	
	private static final String DELETE_ATTIVITA ="DELETE FROM attivita WHERE (id_attivita = ?)";
	
	private static final String UPDATE_ATTIVITA ="UPDATE attivita SET Nome = ?, Descrizione = ?, Citta = ?, Prov = ?, Data = ?, Orario = ?, Prezzo = ?, Max_partecipanti = ? WHERE (id_attivita = ?)";
	
	private static final String INSERT_ATTIVITA = "INSERT INTO attivita (Nome, Descrizione, Citta, Prov, Data, Orario, Prezzo, Max_partecipanti, id_cicerone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public Utente trovaUtente(String email) {
		return jdbc.query(SELECT_UTENTE_BY_EMAIL, new UtenteMapper(), email);
	}
	
	public int trovaIDUtente(String email) {
		return jdbc.queryForObject(
                SELECT_ID_UTENTE_BY_EMAIL, new Object[] { email }, Integer.class);
	}
	
	public List<Attivita> trovaAttivita(int idUtente) {
		return jdbc.query(SELECT_ATTIVITA, new AttivitaMapper(), idUtente);
	}
	
	public void inserisciUtente(Utente utente) {
		jdbc.update(INSERT_UTENTE, utente.getNome(), utente.getCognome(), utente.getEmail(), utente.getPassword(), utente.getCellulare());
	}

	
	public void salvaUtente(Utente utente) {
		Object[] params = { utente.getNome(),utente.getCognome(), utente.getEmail(),utente.getPassword(), utente.getCellulare(), utente.getIdUtente()};
        int[] types = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT};
        jdbc.update(UPDATE_UTENTE, params, types);
	}
	
	public void salvaAttivita(Attivita attivita) {
		Object[] params = { attivita.getNome(),attivita.getDescrizione(), attivita.getCitta(),attivita.getProv(), attivita.getData(), attivita.getOrario(), attivita.getPrezzo(), attivita.getMaxPartecipanti(), attivita.getIdAttivita()};
        int[] types = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.TIME,Types.VARCHAR, Types.BIGINT, Types.BIGINT};
        jdbc.update(UPDATE_ATTIVITA, params, types);
	}
	
	public void insertAttivita(Attivita attivita, int idCicerone) {
		jdbc.update(INSERT_ATTIVITA, attivita.getNome(), attivita.getDescrizione(), attivita.getCitta(), attivita.getProv(), attivita.getData(), attivita.getOrario(), attivita.getPrezzo(), attivita.getMaxPartecipanti(), idCicerone);
	}
	
	public void deleteAttivita(int id){
		Object[] params = {id};
		int[] types = {Types.BIGINT};
		jdbc.update(DELETE_ATTIVITA, params, types);
	}

	public void disattivaProfilo(int idUtente) {
		jdbc.update(DISATTIVA_UTENTE, new Object[] { idUtente });
		
	}
}
