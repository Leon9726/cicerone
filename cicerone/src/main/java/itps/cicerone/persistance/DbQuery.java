package itps.cicerone.persistance;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import itps.cicerone.model.Attivita;
import itps.cicerone.model.AttivitaMapper;
import itps.cicerone.model.AttivitaRicercate;
import itps.cicerone.model.AttivitaRicercateMapper;
import itps.cicerone.model.Feedback;
import itps.cicerone.model.FeedbackMapper;
import itps.cicerone.model.Utente;
import itps.cicerone.model.UtenteMapper;

@Component
public class DbQuery {

	@Autowired
	JdbcTemplate jdbc;

	private static final String SELECT_UTENTE_BY_EMAIL = "select * from utenti where Email=?";

	private static final String SELECT_UTENTE = "select * from utenti where Email=? and id_utente <> ?";

	private static final String DISATTIVA_UTENTE = "UPDATE utenti SET stato = 0 WHERE (id_utente = ?)";

	private static final String SELECT_ID_UTENTE_BY_EMAIL = "select id_utente from utenti where Email=?";

	private static final String INSERT_UTENTE = "INSERT INTO utenti (Nome, Cognome, Email, Password, Cellulare) VALUES (?, ?, ?, ?, ?)";

	private static final String UPDATE_UTENTE = "UPDATE utenti SET Nome = ?, Cognome = ?, Email = ?, Password = ?, Cellulare = ? WHERE (id_utente = ?)";

	private static final String SELECT_ATTIVITA = "select * from attivita where id_cicerone=?";

	private static final String SELECT_ATTIVITA_BY_ID = "select * from attivita where id_attivita=?";

	private static final String DELETE_ATTIVITA = "DELETE FROM attivita WHERE (id_attivita = ?)";

	private static final String UPDATE_ATTIVITA = "UPDATE attivita SET Nome = ?, Descrizione = ?, Citta = ?, Prov = ?, Data = ?, Orario = ?, Prezzo = ?, Max_partecipanti = ? WHERE (id_attivita = ?)";

	private static final String INSERT_ATTIVITA = "INSERT INTO attivita (Nome, Descrizione, Citta, Prov, Data, Orario, Prezzo, Max_partecipanti, id_cicerone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String INSERT_ATTIVITA_PRENOTATA = "INSERT INTO prenotazioni (id_attivita, id_utente, id_cicerone, Num_partecipanti) VALUES (?, ?, ?, ?)";

	private static final String SELECT_EMAIL_BY_ATTIVITA = "select utenti.Email from cicerone.attivita, cicerone.utenti where id_attivita=? and (id_cicerone = utenti.id_utente)";

	private static final String SELECT_NUM_POSTI = "select posti_prenotati, Max_partecipanti from attivita where id_attivita=?";

	private static final String UPDATE_STATO_PRENOTAZIONE = "UPDATE prenotazioni SET Stato = ? WHERE (id_prenotazione = ?)";

	private static final String UPDATE_NUM_PRENOTATI = "UPDATE attivita SET posti_prenotati = ? WHERE (id_attivita = ?)";

	private static final String SELECT_PRENOTAZIONI = "select attivita.posti_prenotati, prenotazioni.Num_partecipanti, prenotazioni.id_attivita from cicerone.prenotazioni LEFT JOIN attivita ON attivita.id_attivita = prenotazioni.id_attivita where id_prenotazione=?";

	private static final String INSERT_ATTIVITA_SALVATE = "INSERT INTO attivita_salvate (id_utente, id_attivita, id_cicerone) VALUES (?, ?, ?)";

	private static final String ATTIVITA_SALVATE = "select idattivita_salvata, utenti.nome as NomeCicerone, utenti.cognome,utenti.Email, attivita.* from cicerone.attivita_salvate left join utenti on utenti.id_utente = attivita_salvate.id_utente left join attivita on attivita.id_attivita=attivita_salvate.id_attivita where attivita_salvate.id_utente=?";

	private static final String DELETE_ATTIVITA_SALVATE = "DELETE FROM attivita_salvate WHERE (idattivita_salvata = ?)";

	private static final String INSERT_FEEDBACK = "INSERT INTO feedback (id_utente, id_cicerone, punteggio, didascalia) VALUES (?, ?, ?, ?)";

	private static final String DELETE_PRENOTAZIONE = "DELETE FROM prenotazioni WHERE (id_prenotazione = ?)";

	private static final String SELECT_FEEDBACK = "select nome, cognome, punteggio, didascalia from cicerone.feedback left join utenti on utenti.id_utente = feedback.id_utente where id_cicerone=?";

	private static final String COUNT_PRENOTAZIONE = "SELECT count(id_prenotazione) FROM cicerone.prenotazioni where id_cicerone=? and Stato = 'in attesa';";

	private static final String AND = " and ";

	public Utente trovaUtente(String email) {
		return jdbc.query(SELECT_UTENTE_BY_EMAIL, new UtenteMapper(), email);
	}

	public List<Feedback> trovaFeedback(int idCicerone) {
		return jdbc.query(SELECT_FEEDBACK, new FeedbackMapper(), idCicerone);
	}

	public List<AttivitaRicercate> trovaAttivitaSalvate(int idUtente) {
		return jdbc.query(ATTIVITA_SALVATE, new AttivitaRicercateMapper("salvate"), idUtente);
	}

	public int trovaIDUtente(String email) {
		return jdbc.queryForObject(SELECT_ID_UTENTE_BY_EMAIL, new Object[] { email }, Integer.class);
	}

	public int countPrenotazioni(int id) {
		return jdbc.queryForObject(COUNT_PRENOTAZIONE, new Object[] { id }, Integer.class);
	}

	public String trovaEmailUtente(int idAttivita) {
		return jdbc.queryForObject(SELECT_EMAIL_BY_ATTIVITA, new Object[] { idAttivita }, String.class);
	}

	public void updateStato(String stato, int idPrenotazione) {
		jdbc.update(UPDATE_STATO_PRENOTAZIONE, stato, idPrenotazione);
	}

	public void deleteAttivitaSalvata(int idAttivitaSalvata) {
		jdbc.update(DELETE_ATTIVITA_SALVATE, idAttivitaSalvata);
	}

	public void deletePrenotazione(int idPrenotazione) {
		jdbc.update(DELETE_PRENOTAZIONE, idPrenotazione);
	}

	public List<Attivita> trovaAttivita(int idUtente) {
		return jdbc.query(SELECT_ATTIVITA, new AttivitaMapper(), idUtente);
	}

	public List<Attivita> trovaAttivitaId(int idAttivita) {
		return jdbc.query(SELECT_ATTIVITA_BY_ID, new AttivitaMapper(), idAttivita);
	}

	public void inserisciUtente(Utente utente) {
		jdbc.update(INSERT_UTENTE, utente.getNome(), utente.getCognome(), utente.getEmail(), utente.getPassword(),
				utente.getCellulare());
	}

	public void inserisciAttivitaSalvata(int idAttivita, int idUtente, int idCicerone) {
		jdbc.update(INSERT_ATTIVITA_SALVATE, idUtente, idAttivita, idCicerone);
	}

	public void inserisciFeedback(int idUtente, int idCicerone, float valutazione, String descrizione) {
		jdbc.update(INSERT_FEEDBACK, idUtente, idCicerone, valutazione, descrizione);
	}

	public Map<String, Object> selectPosti(int idAttivita) {
		List<Map<String, Object>> list = jdbc.queryForList(SELECT_NUM_POSTI, idAttivita);
		return list.get(0);
	}

	public Map<String, Object> selectNumPrenotazioni(int idPrenotazione) {
		List<Map<String, Object>> list = jdbc.queryForList(SELECT_PRENOTAZIONI, idPrenotazione);
		return list.get(0);
	}

	public void salvaUtente(Utente utente) {
		Object[] params = { utente.getNome(), utente.getCognome(), utente.getEmail(), utente.getPassword(),
				utente.getCellulare(), utente.getIdUtente() };
		int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT };
		jdbc.update(UPDATE_UTENTE, params, types);
	}

	public void salvaAttivita(Attivita attivita) {
		Object[] params = { attivita.getNome(), attivita.getDescrizione(), attivita.getCitta(), attivita.getProv(),
				attivita.getData(), attivita.getOrario(), attivita.getPrezzo(), attivita.getMaxPartecipanti(),
				attivita.getIdAttivita() };
		int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.TIME,
				Types.VARCHAR, Types.BIGINT, Types.BIGINT };
		jdbc.update(UPDATE_ATTIVITA, params, types);
	}

	public void updatePostiPrenotazione(int idAttivita, int posti) {
		Object[] params = { posti, idAttivita };
		int[] types = { Types.BIGINT, Types.BIGINT };
		jdbc.update(UPDATE_NUM_PRENOTATI, params, types);
	}

	public void insertAttivita(Attivita attivita, int idCicerone) {
		jdbc.update(INSERT_ATTIVITA, attivita.getNome(), attivita.getDescrizione(), attivita.getCitta(),
				attivita.getProv(), attivita.getData(), attivita.getOrario(), attivita.getPrezzo(),
				attivita.getMaxPartecipanti(), idCicerone);
	}

	public void deleteAttivita(int id) {
		Object[] params = { id };
		int[] types = { Types.BIGINT };
		jdbc.update(DELETE_ATTIVITA, params, types);
	}

	public void disattivaProfilo(int idUtente) {
		jdbc.update(DISATTIVA_UTENTE, idUtente);

	}

	public List<AttivitaRicercate> ricercaAttivita(Map<String, String> parametri, int idUtente) {
		return jdbc.query(createQuerySearchAttivita(parametri, idUtente), new AttivitaRicercateMapper("ricercate"),
				null);
	}

	public List<AttivitaRicercate> ricercaAttivitaPrenotate(int id, int tipologia) {
		StringBuilder sql = new StringBuilder(
				"SELECT id_prenotazione, prenotazioni.Stato, Num_partecipanti, attivita.*, utenti.nome as NomeCicerone, utenti.cognome, utenti.Email FROM cicerone.prenotazioni ,cicerone.attivita, cicerone.utenti where prenotazioni.id_attivita = attivita.id_attivita and prenotazioni.id_cicerone = utenti.id_utente ");
		if (tipologia == 0) {
			sql.append("and prenotazioni.id_cicerone=? and prenotazioni.Stato = 'in attesa'");
		} else if (tipologia == 1) {
			sql.append("and prenotazioni.id_utente=?");
		} else {
			sql.append("and prenotazioni.id_attivita=? ");
		}
		return jdbc.query(sql.toString(), new AttivitaRicercateMapper("prenotazioni"), id);
	}

	public void inserisciPrenotazione(int idAttivita, int idUtente, int idCicerone, int numPartecipanti) {
		jdbc.update(INSERT_ATTIVITA_PRENOTATA, idAttivita, idUtente, idCicerone, numPartecipanti);
	}

	public String createQuerySearchAttivita(Map<String, String> parametri, int idUtente) {
		List<String> chiavi = new ArrayList<String>();
		StringBuilder sql = new StringBuilder(
				"SELECT attivita.*, utenti.nome as NomeCicerone, utenti.cognome, utenti.Email from attivita LEFT JOIN utenti ON utenti.id_utente=attivita.id_cicerone  left join prenotazioni on attivita.id_attivita =prenotazioni.id_attivita where (prenotazioni.id_utente <> ")
						.append("'").append(idUtente).append("'")
						.append(" or prenotazioni.id_utente is null or prenotazioni.Stato = 'respinta') and Max_partecipanti <> attivita.posti_prenotati and ");
		sql.append("'").append(idUtente).append("'").append("<> attivita.id_cicerone");
		if (!parametri.get("citta").equals("")) {
			chiavi.add("citta");
		}
		if (!parametri.get("prov").equals("")) {
			chiavi.add("prov");
		}
		if (!parametri.get("data").equals("")) {
			chiavi.add("data");
		}
		if (!parametri.get("prezzo").equals("")) {
			chiavi.add("prezzo");
		}
		for (int i = 0; i < chiavi.size(); i++) {
			if (chiavi.size() == 1) {
				if(chiavi.get(i).equalsIgnoreCase("prezzo")){
					sql.append(AND).append(chiavi.get(i)).append(" < ").append("'").append(parametri.get(chiavi.get(i))).append("'")
					.append(";");
				} else {
					sql.append(AND).append(chiavi.get(i)).append(" = ").append("'").append(parametri.get(chiavi.get(i))).append("'")
					.append(";");
				}
			} else if (i == (chiavi.size() - 1)) {
				if(chiavi.get(i).equalsIgnoreCase("prezzo")){
					sql.append(AND).append(chiavi.get(i)).append(" < ").append("'").append(parametri.get(chiavi.get(i))).append("'")
					.append(";");
				} else {
				sql.append(chiavi.get(i)).append(" = ").append("'").append(parametri.get(chiavi.get(i))).append("'")
						.append(";");
				}
			} else {
				if(chiavi.get(i).equalsIgnoreCase("prezzo")){
					sql.append(AND).append(chiavi.get(i)).append(" < ").append("'").append(parametri.get(chiavi.get(i))).append("'")
					.append(";");
				} else {
				sql.append(AND).append(chiavi.get(i)).append(" = ").append("'").append(parametri.get(chiavi.get(i)))
						.append("'").append(AND);
				}
			}
		}
		return sql.toString();
	}

	public boolean esisteUtente(String email, int idUtente) {
		Utente u = jdbc.query(SELECT_UTENTE, new UtenteMapper(), email, idUtente);
		if (u == null) {
			return false;
		}
		return true;
	}
}
