package itps.cicerone.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UtenteMapper implements ResultSetExtractor<Utente> {

	public Utente extractData(ResultSet rs) throws SQLException, DataAccessException {
		Utente utente = null;
		if(rs.next()){
			utente= new Utente();
			utente.setIdUtente(rs.getInt("id_utente"));
			utente.setNome(rs.getString("Nome"));
			utente.setCognome(rs.getString("Cognome"));
			utente.setEmail(rs.getString("Email"));
			utente.setPassword(rs.getString("Password"));
			utente.setCellulare(rs.getString("Cellulare"));
			utente.setStato(rs.getInt("stato"));
		}
		return utente;
	}

}
