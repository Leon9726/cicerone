package itps.cicerone.controller;

import java.util.ArrayList;
import java.util.List;

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

import itps.cicerone.model.Attivita;
import itps.cicerone.model.Utente;
import itps.cicerone.persistance.DbQuery;

@Controller
public class CiceroneController {

	@Autowired
	DbQuery dbQuery;
	
	Utente utente = new Utente();
	
	List<Attivita> attivitaList = new ArrayList<Attivita>();

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String homePage(Model model) {
		model.addAttribute("utente", new Utente());
		return "login";
	}

	@RequestMapping(value = "/registra", method = RequestMethod.POST)
	public String registraUtente(@Valid @ModelAttribute("utente") Utente utenteValid, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("isRegistra",true);
			return "login";
		}
		utente = dbQuery.trovaUtente(utente.getEmail());
		if (utente == null) {
			utente=utenteValid;
			dbQuery.inserisciUtente(utente);
			utente.setIdUtente(dbQuery.trovaIDUtente(utente.getEmail()));
			model.addAttribute("utente", utente);
			model.addAttribute("isRegistra",false);
			return "home";
		}
		model.addAttribute("isRegistra",true);
		return "login";
	}

	@RequestMapping(value = "/entra", method = RequestMethod.POST)
	public String entra(@RequestParam("emaiLogin") String email, @RequestParam("pwdLogin") String pwd, Model model) {
		StringBuilder stringBuilder = new StringBuilder("");
		 utente = dbQuery.trovaUtente(email);
		if(utente !=null){
			if (!utente.getPassword().equals(pwd)) {
				System.out.println(stringBuilder.append("Password non comabace per l'utente : ").append(utente.getCognome())
						.append(" ").append(utente.getNome()));
				model.addAttribute("utente", new Utente());
				return "redirect:/login";
			} else if(utente.getStato() == 0){
				System.out.println(stringBuilder.append("Utente disattivato"));
				model.addAttribute("utente", new Utente());
				return "redirect:/login";
			}
		} else {
				System.out.println(stringBuilder.append("Utente non esistente : "));
				model.addAttribute("utente", new Utente());
				return "redirect:/login";
		}
		model.addAttribute("utente", utente);
		return "home";
	}
	
	@RequestMapping(value="/profilo", method= RequestMethod.GET)
	public String profilo(Model model){
		model.addAttribute("utente", utente);
		return "profilo";
	}
	
	@RequestMapping(value="/salvaUtente", method = RequestMethod.POST)
	private String salvaUtente(@Valid @ModelAttribute("utente") Utente utenteUpdate, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			modificaUtente(utenteUpdate);
			dbQuery.salvaUtente(utente);
		}
		model.addAttribute("utente", utente);
		return "profilo";
	}
	
	@RequestMapping(value="/salvaAttivita", method = RequestMethod.POST)
	private String salvaAttivita(@Valid @ModelAttribute("attivita") Attivita attivita, BindingResult result, Model model) {
		model.addAttribute("utente", utente);
		if (!result.hasErrors()) {
			if(attivita.getIdAttivita() != 0) {
				dbQuery.salvaAttivita(attivita);
			} else {
				dbQuery.insertAttivita(attivita, utente.getIdUtente());
			}
			return "redirect:/attivita";
		}
		  return "modifica-attivita";
	}
	
	@RequestMapping(value="/pageRicercaAttivita", method = RequestMethod.GET)
	private String pageRicercaAttivita(Model model){
		model.addAttribute("utente", utente);
		return "ricerca-attivita";
	} 
	
	@RequestMapping(value="/ricercaAttivita", method= RequestMethod.POST)
	private String ricercaAttivita(Model model) {
		model.addAttribute("utente", utente);
		return "ricerca-attivita";
	}
	
	@RequestMapping(value="/home", method= RequestMethod.GET)
	private String home(Model model){
		model.addAttribute("utente", utente);
		return "home";
	}
	
	@RequestMapping(value="/attivita", method=RequestMethod.GET)
	private String attivita(Model model) {
		attivitaList = dbQuery.trovaAttivita(utente.getIdUtente());
		model.addAttribute("utente", utente);
		model.addAttribute("attivitaList", attivitaList);
		return "attivita";
	}
	
    @RequestMapping(value="doDelete/{id}", method = RequestMethod.GET)
    public String deleteAttivita (@PathVariable int id) {
        dbQuery.deleteAttivita(id);
        return "redirect:/attivita";
    }
    
    @RequestMapping(value="doModify/{id}", method = RequestMethod.GET)
    public String modificaAttivita (@PathVariable int id, Model model) {
    	estraiAttivita(id);
    	model.addAttribute("attivita", estraiAttivita(id));
    	model.addAttribute("utente", utente);
        return "modifica-attivita";
    }
    
    @RequestMapping(value="/aggiungiAttivita", method = RequestMethod.GET)
    public String creaAttivita (Model model) {
    	model.addAttribute("attivita", new Attivita());
    	model.addAttribute("utente", utente);
    	return "modifica-attivita";
    }
    
    @RequestMapping(value="/disattivaProfilo", method = RequestMethod.GET)
    public String disattivaProfilo (Model model) {
    	dbQuery.disattivaProfilo(utente.getIdUtente());
    	utente=new Utente();
    	return "redirect:/login";
    }
    
	private void modificaUtente(Utente utente){
		this.utente.setNome(utente.getNome());
		this.utente.setCognome(utente.getCognome());
		this.utente.setEmail(utente.getEmail());
		this.utente.setPassword(utente.getPassword());
		this.utente.setCellulare(utente.getCellulare());
	}
	
	private Attivita estraiAttivita(int id){
		Attivita attivita = null;
		for (Attivita a : attivitaList) {
			if(a.getIdAttivita() == id) {
				attivita=a;
				break;
			}
		}
		return attivita;
	}
}
