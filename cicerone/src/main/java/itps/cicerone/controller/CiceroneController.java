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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	String errore;

	Utente utente = new Utente();

	List<Attivita> attivitaList = new ArrayList<Attivita>();

	List<AttivitaRicercate> attivitaPrenotate = new ArrayList<AttivitaRicercate>();
	
	int countPrenotazioni = 0;
	
	List<Feedback> feedback = new ArrayList<Feedback>();
	
	private static final String UTENTE_COSTANT="utente";
	
	private static final String LOGIN_COSTANT="login";
	
	private static final String ATT_PREN_COSTANT="attivitaListPrenotate";
	
	private static final String COUNT_PRENOTAZIONI="prenotazioni";
	
	private static final String ATT_COSTANT="attivitaList";
	
	private static final String ERR_COSTANT="errore";
	
	private static final String IS_REGISTRA="isRegistra";
	
	private static final String ERRORE_COST="Errore nei campi inseriti";
	
	private static final String ATTIVITA_MOD="modifica-attivita";
	
	private static final String ATTIVITA_COST="attivita";
	
	private static final String REDIRECT_COST="redirect:/attivitaPrenotate";
	
	private static final String REDIRECT_PAGE_RICERCA="redirect:/pageRicercaAttivita";

	@GetMapping(value = "/login")
	public String homePage(Model model) {
		utente = new Utente();
		model.addAttribute(UTENTE_COSTANT, utente);
		return LOGIN_COSTANT;
	}
	
	@GetMapping(value = "/entraOspite")
	public String entraOspite(Model model) {
		utente.setNome("Ospite");
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		return "home";
	}

	@PostMapping(value = "/registra")
	public String registraUtente(@Valid @ModelAttribute(UTENTE_COSTANT) Utente utenteValid, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute(ERR_COSTANT, ERRORE_COST);
			model.addAttribute(IS_REGISTRA, true);
			return LOGIN_COSTANT;
		}
		utente = dbQuery.trovaUtente(utenteValid.getEmail());
		if (utente == null) {
			utente = utenteValid;
			dbQuery.inserisciUtente(utente);
			utente.setIdUtente(dbQuery.trovaIDUtente(utente.getEmail()));
			attivitaPrenotate = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 0);
			countPrenotazioni = dbQuery.countPrenotazioni(utente.getIdUtente());
			model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
			model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
			model.addAttribute(UTENTE_COSTANT, utente);
			model.addAttribute(IS_REGISTRA, false);
			return "home";
		}
		model.addAttribute(IS_REGISTRA, true);
		return LOGIN_COSTANT;
	}

	@PostMapping(value = "/entra")
	public String entra(@RequestParam("emailLogin") String email, @RequestParam("pwdLogin") String pwd, Model model) {
		StringBuilder stringBuilder = new StringBuilder("");
		utente = dbQuery.trovaUtente(email);
		if (utente != null) {
			if (!utente.getPassword().equals(pwd)) {
				stringBuilder.append("password");
				model.addAttribute(ERR_COSTANT, stringBuilder.toString());
				utente = new Utente();
				model.addAttribute(UTENTE_COSTANT, utente);
				return LOGIN_COSTANT;
			} else if (utente.getStato() == 0) {
				stringBuilder.append("disattivato");
				model.addAttribute(ERR_COSTANT, stringBuilder.toString());
				utente= new Utente();
				model.addAttribute(UTENTE_COSTANT, utente);
				return LOGIN_COSTANT;
			}
		} else {
			stringBuilder.append("inesistente");
			model.addAttribute(ERR_COSTANT, stringBuilder.toString());
			utente= new Utente();
			model.addAttribute(UTENTE_COSTANT, utente);
			return LOGIN_COSTANT;
		}
		countPrenotazioni = dbQuery.countPrenotazioni(utente.getIdUtente());
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		attivitaPrenotate = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 0);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		model.addAttribute(UTENTE_COSTANT, utente);
		return "home";
	}

	@GetMapping(value = "/profilo")
	public String profilo(Model model) {
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		model.addAttribute(UTENTE_COSTANT, utente);
		if(errore!= null) {
			model.addAttribute(ERR_COSTANT, errore);
		}
		return "profilo";
	}

	@PostMapping(value = "/salvaUtente")
	public String salvaUtente(@Valid @ModelAttribute(UTENTE_COSTANT) Utente utenteUpdate, BindingResult result,
			Model model) {
		errore= null;
		if (!result.hasErrors()) {
			if(!dbQuery.esisteUtente(utenteUpdate.getEmail(), utente.getIdUtente())){
				modificaUtente(utenteUpdate);
				dbQuery.salvaUtente(utente);
			} else {
				errore="Email già esistente";
			}
		} else {
				errore = ERRORE_COST;
		}
		return "redirect:/profilo";
	}

	@PostMapping(value = "/salvaAttivita")
	public String salvaAttivita(@Valid @ModelAttribute(ATTIVITA_COST) Attivita attivita, BindingResult result,
			Model model) {
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		if (!result.hasErrors()) {
			if (attivita.getIdAttivita() != 0) {
				dbQuery.salvaAttivita(attivita);
			} else {
				dbQuery.insertAttivita(attivita, utente.getIdUtente());
			}
			return "redirect:/attivita";
		}
		model.addAttribute(ERR_COSTANT, ERRORE_COST);
		return ATTIVITA_MOD;
	}

	@GetMapping(value = "/pageRicercaAttivita")
	public String pageRicercaAttivita(Model model) {
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		model.addAttribute(ERR_COSTANT, errore);
		return "ricerca-attivita";
	}

	@PostMapping(value = "/ricercaAttivita")
	public String ricercaAttivita(Model model, HttpServletRequest request) {
		Map<String, String> parametri = new HashMap<String, String>();
		parametri.put("attivita.Nome", request.getParameter("nome"));
		parametri.put("prov", request.getParameter("prov"));
		parametri.put("citta", request.getParameter("citta"));
		parametri.put("prezzo", request.getParameter("prezzo"));
		parametri.put("data", request.getParameter("data"));
		model.addAttribute(UTENTE_COSTANT, utente);
		List<AttivitaRicercate> attivita = dbQuery.ricercaAttivita(parametri, utente.getIdUtente());
		model.addAttribute(ATT_COSTANT, attivita);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		return "ricerca-attivita";
	}

	@GetMapping(value = "/home")
	public String home(Model model) {
		countPrenotazioni = dbQuery.countPrenotazioni(utente.getIdUtente());
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		attivitaPrenotate = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 0);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		model.addAttribute(UTENTE_COSTANT, utente);
		return "home";
	}

	@GetMapping(value = "/attivita")
	public String attivita(Model model) {
		attivitaList = dbQuery.trovaAttivita(utente.getIdUtente());
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		model.addAttribute(ATT_COSTANT, attivitaList);
		return ATTIVITA_COST;
	}
	
	@GetMapping(value = "/attivitaSalvate")
	public String attivitaSalvate(Model model) {
		List<AttivitaRicercate> attivitaSalvate = dbQuery.trovaAttivitaSalvate(utente.getIdUtente());
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		model.addAttribute(ATT_COSTANT, attivitaSalvate);
		return "attivita-salvate";
	}

	@GetMapping(value = "doDelete/{id}")
	public String deleteAttivita(@PathVariable int id) {
		dbQuery.deleteAttivita(id);
		return "redirect:/attivita";
	}

	@GetMapping(value = "doConfermaPrenotazione/{id}")
	public String confermaPrenotazione(@PathVariable int id) {
		dbQuery.updateStato("accettata", id);
		return "redirect:/home";
	}
	
	@GetMapping(value = "doRifiutaPrenotazione/{id}")
	public String rifiutaPrenotazione(@PathVariable int id) {
		dbQuery.updateStato("respinta", id);
		aggiornaPosti(id);
		return "redirect:/home";
	}

	@GetMapping(value = "doDeletePrenotazione/{id}")
	public String deletePrenotazione(@PathVariable int id) {
		dbQuery.updateStato("annullata", id);
		aggiornaPosti(id);
		return REDIRECT_COST;
	}
	
	@GetMapping(value = "eliminaPrenotazione/{id}")
	public String eliminaPrenotazione(@PathVariable int id) {
		dbQuery.deletePrenotazione(id);
		return REDIRECT_COST;
	}
	
	@GetMapping(value = "doEliminaAttivitaSalvata/{id}")
	public String deleteAttivitaSalvata(@PathVariable int id) {
		dbQuery.deleteAttivitaSalvata(id);
		return "redirect:/attivitaSalvate";
	}
	
	
	@GetMapping(value = "doSalvaAttivita/{id}")
	public String salvaAttivita(@PathVariable int id) {
		Attivita attivita = dbQuery.trovaAttivitaId(id).get(0);
		dbQuery.inserisciAttivitaSalvata(attivita.getIdAttivita(), utente.getIdUtente(), attivita.getIdCicerone());
		return "redirect:/attivitaSalvate";
	}

	@GetMapping(value = "doModify/{id}")
	public String modificaAttivita(@PathVariable int id, Model model) {
		estraiAttivita(id);
		model.addAttribute(ATTIVITA_COST, estraiAttivita(id));
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		return ATTIVITA_MOD;
	}

	@GetMapping(value = "/aggiungiAttivita")
	public String creaAttivita(Model model) {
		model.addAttribute(ATTIVITA_COST, new Attivita());
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, attivitaPrenotate);
		return ATTIVITA_MOD;
	}

	@GetMapping(value = "/attivitaPrenotate")
	public String attivitaPrenotate(Model model) {
		List<AttivitaRicercate> attivitaPrenotateUtente = dbQuery.ricercaAttivitaPrenotate(utente.getIdUtente(), 1);
		model.addAttribute(ATT_COSTANT, attivitaPrenotateUtente);
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, this.attivitaPrenotate);
		return "attivita-prenotate";
	}

	@GetMapping(value = "/disattivaProfilo")
	public String disattivaProfilo(Model model) {
		dbQuery.disattivaProfilo(utente.getIdUtente());
		utente = new Utente();
		return "redirect:/login";
	}
	
	@PostMapping(value = "/inviaFeedback")
	public String inviaFeedback (@RequestParam("idCicerone") int idCicerone, @RequestParam("valutazione") float valutazione, @RequestParam("descrizione") String descrizione) {
			dbQuery.inserisciFeedback(utente.getIdUtente(), idCicerone, valutazione, descrizione);
		return REDIRECT_COST;
	}
	
	@GetMapping(value = "/feedback")
	public String feedback(Model model) {
		List<Feedback> feedbackUtente =  dbQuery.trovaFeedback(utente.getIdUtente());
		model.addAttribute("feedbackList", feedbackUtente);
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, this.attivitaPrenotate);
		model.addAttribute("torna", false);
		return "feedback";
	}
	
	@GetMapping(value = "/pageFeedback")
	public String pageFeedback(Model model) {
		model.addAttribute("torna", true);
		model.addAttribute("feedbackList", feedback);
		model.addAttribute(UTENTE_COSTANT, utente);
		model.addAttribute(COUNT_PRENOTAZIONI, countPrenotazioni);
		model.addAttribute(ATT_PREN_COSTANT, this.attivitaPrenotate);
		return "feedback";
	}
	
	@GetMapping(value = "trovaFeedback/{id}")
	public String feedback(Model model, @PathVariable int id) {
		feedback = dbQuery.trovaFeedback(id);
		return "redirect:/pageFeedback";
	}
	
	@PostMapping(value = "/prenotaAttivita")
	public String prenotaAttivita(@RequestParam("idAttivita") String idAttiv, @RequestParam("idAttivitaSalvate") String idAttivitaSalvate, 
			@RequestParam("idCicerone") String idCicerone, @RequestParam("partecipanti") String numPartecipanti, @RequestParam("tipologia") String tipologia,
			Model model) {
		if(numPartecipanti.equalsIgnoreCase("") || Integer.valueOf(numPartecipanti) == 0) {
			errore="Numero dei partecipanti errato, inserire un numero corretto";
			return REDIRECT_PAGE_RICERCA;
		}
		Map<String, Object> map = dbQuery.selectPosti(Integer.valueOf(idAttiv));
		AttivitaRicercate attivita = new AttivitaRicercate();
		attivita.setPostiPrenotati(Integer.valueOf(String.valueOf(map.get("posti_prenotati"))));
		attivita.setMaxPartecipanti(Integer.valueOf(String.valueOf(map.get("Max_partecipanti"))));
		if ((attivita.getMaxPartecipanti() > Integer.valueOf(numPartecipanti)) && (attivita
				.getMaxPartecipanti() > (Integer.valueOf(numPartecipanti) + attivita.getPostiPrenotati()))) {
			dbQuery.inserisciPrenotazione(Integer.valueOf(idAttiv), utente.getIdUtente(), Integer.valueOf(idCicerone),
					Integer.valueOf(numPartecipanti));
			dbQuery.updatePostiPrenotazione(Integer.valueOf(idAttiv),
					attivita.getPostiPrenotati() + Integer.valueOf(numPartecipanti));
			attivita = dbQuery.ricercaAttivitaPrenotate(Integer.valueOf(idAttiv), 3).get(0);
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
				errore="Errore nell'invio dell'email";
				return REDIRECT_PAGE_RICERCA;
			}
		} else {
			errore="Posti prenotati maggiori del numero massimo";
		}
		if(tipologia.equalsIgnoreCase("Salvate")){
			return "redirect:/doEliminaAttivitaSalvata/"+idAttivitaSalvate;
		}
		return REDIRECT_PAGE_RICERCA;
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
		Map<String, Object> map = dbQuery.selectNumPrenotazioni(idPrenotazione);
		int postiPrenotati= Integer.valueOf(String.valueOf(map.get("posti_prenotati"))) - Integer.valueOf(String.valueOf(map.get("Num_partecipanti")));
		dbQuery.updatePostiPrenotazione(Integer.valueOf(String.valueOf(map.get("id_attivita"))), postiPrenotati);
	}
}
