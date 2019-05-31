package itps.cicerone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CiceroneController {
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String homePage(Model model) {
		return "login";
	}
	
	@RequestMapping(value="/entra", method = RequestMethod.POST)
	public String prova(@RequestParam("emaiLogin") String email,@RequestParam("pwdLogin") String pwd, Model model) {
		model.addAttribute("email", email);
		model.addAttribute("pwd", pwd);
		return "home";
	}
}
