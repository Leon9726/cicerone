package itps.cicerone.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

public class AttivitaRicercateMapper implements ResultSetExtractor<List<AttivitaRicercate>> {
	
	String tipologia = "";
	
	public AttivitaRicercateMapper(String tipologia) {
		this.tipologia = tipologia;
	}

	public List<AttivitaRicercate> extractData(ResultSet rs) throws SQLException {
		List<AttivitaRicercate> attivitaList= new ArrayList<AttivitaRicercate>();
		while(rs.next()){
			AttivitaRicercate attivita = new AttivitaRicercate();
			if(tipologia.equalsIgnoreCase("salvate")) {
				attivita.setIdAttivitaSalvate(rs.getInt("idattivita_salvata"));
			}
			if(tipologia.equalsIgnoreCase("prenotazioni")) {
				attivita.setIdPrenotazione(rs.getInt("id_prenotazione"));
				attivita.setStato(rs.getString("Stato"));
			}
			attivita.setCitta(rs.getString("Citta"));
			attivita.setDescrizione(rs.getString("Descrizione"));
			attivita.setIdAttivita(rs.getInt("id_attivita"));
			attivita.setIdCicerone(rs.getInt("id_cicerone"));
			attivita.setMaxPartecipanti(rs.getInt("Max_partecipanti"));
			attivita.setData(rs.getDate("Data"));
			attivita.setOrario(rs.getTime("Orario"));
			attivita.setNome(rs.getString("Nome"));
			attivita.setProv(rs.getString("Prov"));
			attivita.setPrezzo(rs.getString("Prezzo"));
			attivita.setNomeCicerone(rs.getString("NomeCicerone"));
			attivita.setCognomeCicerone(rs.getString("cognome"));
			attivita.setEmail(rs.getString("Email"));
			attivita.setPostiPrenotati(rs.getInt("posti_prenotati"));
			attivita.setPostiDisponibili(attivita.getMaxPartecipanti()-attivita.getPostiPrenotati());
			attivitaList.add(attivita);
		}
		return attivitaList;
	}

}
