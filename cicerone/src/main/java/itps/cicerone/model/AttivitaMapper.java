package itps.cicerone.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

public class AttivitaMapper implements ResultSetExtractor<List<Attivita>> {

	public List<Attivita> extractData(ResultSet rs) throws SQLException {
		List<Attivita> attivitaList= new ArrayList<Attivita>();
		while(rs.next()){
			Attivita attivita = new Attivita();
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
			attivitaList.add(attivita);
		}
		return attivitaList;
	}

}
