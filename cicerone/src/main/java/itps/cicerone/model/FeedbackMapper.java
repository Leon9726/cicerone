package itps.cicerone.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class FeedbackMapper implements ResultSetExtractor<List<Feedback>> {

	public List<Feedback> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<Feedback> feedbackList = new ArrayList<Feedback>();
		while(rs.next()){
			Feedback feedback = new Feedback();
			feedback.setCognome(rs.getString("cognome"));
			feedback.setNome(rs.getString("nome"));
			feedback.setDescrizione(rs.getString("didascalia"));
			feedback.setPunteggio(rs.getInt("punteggio"));
			feedbackList.add(feedback);
		}
		return feedbackList;
	}

}
