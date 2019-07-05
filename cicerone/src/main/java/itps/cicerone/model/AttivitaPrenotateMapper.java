package itps.cicerone.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class AttivitaPrenotateMapper implements ResultSetExtractor<List<AttivitaRicercate>> {

	public List<AttivitaRicercate> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<AttivitaRicercate> attivitaList= new ArrayList<AttivitaRicercate>();
		while(rs.next()){
			AttivitaRicercate attivita = new AttivitaRicercate();
			attivita.setIdPrenotazione(rs.getInt("id_prenotazione"));
			attivita.setPartecipantiPrenotati(rs.getInt("Num_partecipanti"));
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
			attivita.setNomeCicerone(rs.getString("nome"));
			attivita.setCognomeCicerone(rs.getString("cognome"));
			attivita.setEmail(rs.getString("Email"));
			attivita.setStato(rs.getString("Stato"));
			attivitaList.add(attivita);
		}
		return attivitaList;
	}

}
