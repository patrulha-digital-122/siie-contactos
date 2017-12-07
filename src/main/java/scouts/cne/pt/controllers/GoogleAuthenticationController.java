package scouts.cne.pt.controllers;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAuthenticationController {
	@GetMapping("/Callback")
	public String getMessageOfTheDay(Principal principal) {
		return "The message of the day is boring for user: " ;
	}
}
