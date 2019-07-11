package itps.cicerone.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import itps.cicerone.email.EmailProvider;
import itps.cicerone.model.Attivita;
import itps.cicerone.model.AttivitaRicercate;
import itps.cicerone.model.Feedback;
import itps.cicerone.model.Utente;
import itps.cicerone.persistance.DbQuery;

@Controller
public class CiceroneController {

	@Autowired
	DbQuery dbQuery;

	@Autowired
	EmailProvider emailProvider;

	Utente utente = new Utente();

	List<Attivita> attivitaList = new ArrayList<Attivita>();

	List<AttivitaRicercate> attivitaPrenotate = new ArrayList<AttivitaRicercate>();
	
	List<Feedback> feedback = new ArrayList<Feedback>();

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String homePage(Model model) {
		model.addAttribute("utente", new Utente());
		return "login";
	}
	
	@RequestMapping(value = "/entraOspite", method = RequestMethod.GET)
	public String entraOspite(Model model) {
		utente.setNome("Ospite");
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		return "home";
	}

	@RequestMapping(value = "/registra", method = RequestMethod.POST)
	public String registraUtente(@Valid @ModelAttribute("utente") Utente utenteValid, BindingResult result,
			Model model) {
		model.addAttribute("attivitaList", attivitaPrenotate);
		if (result.hasErrors()) {
			model.addAttribute("errore", "Errore nei campi inseriti");
			model.addAttribute("isRegistra", true);
			return "login";
		}
		utente = dbQuery.trovaUtente(utenteValid.getEmail());
		if (utente == null) {
			utente = utenteValid;
			dbQuery.inserisciUtente(utente);
			utente.setIdUtente(dbQuery.trovaIDUtente(utente.getEmail()));
			attivitaPrenotate = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 0);
			model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
			model.addAttribute("utente", utente);
			model.addAttribute("isRegistra", false);
			return "home";
		}
		model.addAttribute("isRegistra", true);
		return "login";
	}

	@RequestMapping(value = "/entra", method = RequestMethod.POST)
	public String entra(@RequestParam("emaiLogin") String email, @RequestParam("pwdLogin") String pwd, Model model) {
		StringBuilder stringBuilder = new StringBuilder("");
		utente = dbQuery.trovaUtente(email);
		if (utente != null) {
			if (!utente.getPassword().equals(pwd)) {
				stringBuilder.append("Password non comabace per l'utente : ")
						.append(utente.getCognome()).append(" ").append(utente.getNome());
				model.addAttribute("errore", stringBuilder.toString());
				model.addAttribute("utente", new Utente());
				return "login";
			} else if (utente.getStato() == 0) {
				stringBuilder.append("Utente disattivato");
				model.addAttribute("errore", stringBuilder.toString());
				model.addAttribute("utente", new Utente());
				return "login";
			}
		} else {
			stringBuilder.append("Utente non esistente");
			model.addAttribute("errore", stringBuilder.toString());
			model.addAttribute("utente", new Utente());
			return "login";
		}
		attivitaPrenotate = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 0);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		model.addAttribute("utente", utente);
		return "home";
	}

	@RequestMapping(value = "/profilo", method = RequestMethod.GET)
	public String profilo(Model model) {
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		model.addAttribute("utente", utente);
		return "profilo";
	}

	@RequestMapping(value = "/salvaUtente", method = RequestMethod.POST)
	private String salvaUtente(@Valid @ModelAttribute("utente") Utente utenteUpdate, BindingResult result,
			Model model) {
		if (!result.hasErrors()) {
			modificaUtente(utenteUpdate);
			dbQuery.salvaUtente(utente);
		} else {
			model.addAttribute("errore", "Errore nei campi inseriti");
		}
		return "redirect:/profilo";
	}

	@RequestMapping(value = "/salvaAttivita", method = RequestMethod.POST)
	private String salvaAttivita(@Valid @ModelAttribute("attivita") Attivita attivita, BindingResult result,
			Model model) {
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		if (!result.hasErrors()) {
			if (attivita.getIdAttivita() != 0) {
				dbQuery.salvaAttivita(attivita);
			} else {
				dbQuery.insertAttivita(attivita, utente.getIdUtente());
			}
			return "redirect:/attivita";
		}
		model.addAttribute("errore", "Errore nei campi inseriti");
		return "modifica-attivita";
	}

	@RequestMapping(value = "/pageRicercaAttivita", method = RequestMethod.GET)
	private String pageRicercaAttivita(Model model) {
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		return "ricerca-attivita";
	}

	@RequestMapping(value = "/ricercaAttivita", method = RequestMethod.POST)
	private String ricercaAttivita(Model model, HttpServletRequest request) {
		Map<String, String> parametri = new HashMap<String, String>();
		parametri.put("nome", request.getParameter("nome"));
		parametri.put("prov", request.getParameter("prov"));
		parametri.put("citta", request.getParameter("citta"));
		parametri.put("prezzo", request.getParameter("prezzo"));
		parametri.put("data", request.getParameter("data"));
		model.addAttribute("utente", utente);
		List<AttivitaRicercate> attivita = dbQuery.ricercaAttivita(parametri, utente.getIdUtente());
		model.addAttribute("attivitaList", attivita);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		return "ricerca-attivita";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	private String home(Model model) {
		attivitaPrenotate = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 0);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		model.addAttribute("utente", utente);
		return "home";
	}

	@RequestMapping(value = "/attivita", method = RequestMethod.GET)
	private String attivita(Model model) {
		attivitaList = dbQuery.trovaAttivita(utente.getIdUtente());
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		model.addAttribute("attivitaList", attivitaList);
		return "attivita";
	}
	
	@RequestMapping(value = "/attivitaSalvate", method = RequestMethod.GET)
	private String attivitaSalvate(Model model) {
		List<AttivitaRicercate> attivitaSalvate = dbQuery.trovaAttivitaSalvate(utente.getIdUtente());
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		model.addAttribute("attivitaList", attivitaSalvate);
		return "attivita-salvate";
	}

	@RequestMapping(value = "doDelete/{id}", method = RequestMethod.GET)
	public String deleteAttivita(@PathVariable int id) {
		dbQuery.deleteAttivita(id);
		return "redirect:/attivita";
	}

	@RequestMapping(value = "doConfermaPrenotazione/{id}", method = RequestMethod.GET)
	public String confermaPrenotazione(@PathVariable int id) {
		dbQuery.updateStato("accettata", id);
		return "redirect:/home";
	}
	
	@RequestMapping(value = "doRifiutaPrenotazione/{id}", method = RequestMethod.GET)
	public String rifiutaPrenotazione(@PathVariable int id) {
		dbQuery.updateStato("respinta", id);
		aggiornaPosti(id);
		return "redirect:/home";
	}

	@RequestMapping(value = "doDeletePrenotazione/{id}", method = RequestMethod.GET)
	public String deletePrenotazione(@PathVariable int id) {
		dbQuery.updateStato("annullata", id);
		aggiornaPosti(id);
		return "redirect:/attivitaPrenotate";
	}
	
	@RequestMapping(value = "eliminaPrenotazione/{id}", method = RequestMethod.GET)
	public String eliminaPrenotazione(@PathVariable int id) {
		dbQuery.deletePrenotazione(id);
		return "redirect:/attivitaPrenotate";
	}
	
	@RequestMapping(value = "doEliminaAttivitaSalvata/{id}", method = RequestMethod.GET)
	public String deleteAttivitaSalvata(@PathVariable int id) {
		dbQuery.deleteAttivitaSalvata(id);
		return "redirect:/attivitaSalvate";
	}
	
	
	@RequestMapping(value = "doSalvaAttivita/{id}", method = RequestMethod.GET)
	public String salvaAttivita(@PathVariable int id) {
		Attivita attivita = dbQuery.trovaAttivitaId(id).get(0);
		dbQuery.inserisciAttivitaSalvata(attivita.getIdAttivita(), utente.getIdUtente(), attivita.getIdCicerone());
		return "redirect:/attivitaSalvate";
	}

	@RequestMapping(value = "doModify/{id}", method = RequestMethod.GET)
	public String modificaAttivita(@PathVariable int id, Model model) {
		estraiAttivita(id);
		model.addAttribute("attivita", estraiAttivita(id));
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		return "modifica-attivita";
	}

	@RequestMapping(value = "/aggiungiAttivita", method = RequestMethod.GET)
	public String creaAttivita(Model model) {
		model.addAttribute("attivita", new Attivita());
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", attivitaPrenotate);
		return "modifica-attivita";
	}

	@RequestMapping(value = "/attivitaPrenotate", method = RequestMethod.GET)
	public String attivitaPrenotate(Model model) {
		List<AttivitaRicercate> attivitaPrenotate = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 1);
		model.addAttribute("attivitaList", attivitaPrenotate);
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", this.attivitaPrenotate);
		return "attivita-prenotate";
	}

	@RequestMapping(value = "/disattivaProfilo", method = RequestMethod.GET)
	public String disattivaProfilo(Model model) {
		dbQuery.disattivaProfilo(utente.getIdUtente());
		utente = new Utente();
		return "redirect:/login";
	}
	
	@RequestMapping(value = "/inviaFeedback", method = RequestMethod.POST)
	public String inviaFeedback (@RequestParam("idCicerone") int idCicerone, @RequestParam("valutazione") float valutazione, @RequestParam("descrizione") String descrizione) {
			dbQuery.inserisciFeedback(utente.getIdUtente(), idCicerone, valutazione, descrizione);
		return "redirect:/attivitaPrenotate";
	}
	
	@RequestMapping(value = "/feedback", method = RequestMethod.GET)
	public String feedback(Model model) {
		List<Feedback> feedback = dbQuery.trovaFeedback(utente.getIdUtente());
		model.addAttribute("feedbackList", feedback);
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", this.attivitaPrenotate);
		model.addAttribute("torna", false);
		return "feedback";
	}
	
	@RequestMapping(value = "/pageFeedback", method = RequestMethod.GET)
	public String pageFeedback(Model model) {
		model.addAttribute("torna", true);
		model.addAttribute("feedbackList", feedback);
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaListPrenotate", this.attivitaPrenotate);
		return "feedback";
	}
	
	@RequestMapping(value = "trovaFeedback/{id}", method = RequestMethod.GET)
	public String feedback(Model model, @PathVariable int id) {
		feedback = dbQuery.trovaFeedback(id);
		return "redirect:/pageFeedback";
	}

	@RequestMapping(value = "/prenotaAttivita", method = RequestMethod.POST)
	public String prenotaAttivita(@RequestParam("idAttivita") String idAttiv, @RequestParam("idAttivitaSalvate") String idAttivitaSalvate, 
			@RequestParam("idCicerone") String idCicerone, @RequestParam("partecipanti") String numPartecipanti, @RequestParam("tipologia") String tipologia,
			Model model) {
		Map<String, Object> map = dbQuery.selectPosti(Integer.valueOf(idAttiv));
		AttivitaRicercate attivita = new AttivitaRicercate();
		attivita.setPostiPrenotati(Integer.valueOf(String.valueOf(map.get("posti_prenotati"))));
		attivita.setMaxPartecipanti(Integer.valueOf(String.valueOf(map.get("Max_partecipanti"))));
		if (!(attivita.getMaxPartecipanti() < Integer.valueOf(numPartecipanti)) && !(attivita
				.getMaxPartecipanti() < (Integer.valueOf(numPartecipanti) + attivita.getPostiPrenotati()))) {
			dbQuery.inserisciPrenotazione(Integer.valueOf(idAttiv), utente.getIdUtente(), Integer.valueOf(idCicerone),
					Integer.valueOf(numPartecipanti));
			dbQuery.updatePostiPrenotazione(Integer.valueOf(idAttiv),
					attivita.getPostiPrenotati() + Integer.valueOf(numPartecipanti));
			attivita = dbQuery.ricercaAttivitaPrenotate(Integer.valueOf(idAttiv), 3).get(0);
			;
			String emailCicerone = dbQuery.trovaEmailUtente(Integer.valueOf(idAttiv));
			StringBuilder stringBuilder = new StringBuilder("L'utente ").append(utente.getNome()).append(" ")
					.append(utente.getCognome()).append(" richiede di prenotare l'attività : ")
					.append(attivita.getNome()).append(" per ").append(numPartecipanti).append(" partecipanti .")
					.append("\n")
					.append("Per confermare la prenotazione andare nelle propria area e accettare la richiesta.")
					.append("\n\n").append("Grazie e buona giornata.");
			try {
				emailProvider.send(emailCicerone, "Richiesta prenotazione attivita", stringBuilder.toString());
			} catch (MessagingException e) {
				model.addAttribute("errore", "Errore nell'invio dell'email");
				return "redirect:/pageRicercaAttivita";
			}
		} else {
			model.addAttribute("errore", "Posti prenotati maggiori al numero massimo");
		}
		if(tipologia.equalsIgnoreCase("Salvate")){
			return "redirect:/doEliminaAttivitaSalvata/"+idAttivitaSalvate;
		}
		return "redirect:/pageRicercaAttivita";
	}

	private void modificaUtente(Utente utente) {
		this.utente.setNome(utente.getNome());
		this.utente.setCognome(utente.getCognome());
		this.utente.setEmail(utente.getEmail());
		this.utente.setPassword(utente.getPassword());
		this.utente.setCellulare(utente.getCellulare());
	}

	private Attivita estraiAttivita(int id) {
		Attivita attivita = null;
		for (Attivita a : attivitaList) {
			if (a.getIdAttivita() == id) {
				attivita = a;
				break;
			}
		}
		return attivita;
	}
	
	private void aggiornaPosti(int idPrenotazione) {
		Map<String, Object> map = dbQuery.selectNumPrenotazioni(Integer.valueOf(idPrenotazione));
		int postiPrenotati= Integer.valueOf(String.valueOf(map.get("posti_prenotati"))) - Integer.valueOf(String.valueOf(map.get("Num_partecipanti")));
		dbQuery.updatePostiPrenotazione(Integer.valueOf(String.valueOf(map.get("id_attivita"))), postiPrenotati);
	}
}
