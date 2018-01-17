package scouts.cne.pt.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAuthenticationController {
	//	@GetMapping("/Callback")
	//	public String getMessageOfTheDay(Principal principal) {
	//		return "The message of the day is boring for user: " ;
	//	}

	@RequestMapping(value = "/Callback", method = RequestMethod.GET)
	public void callback(@RequestParam(value = "code", required = true) String code) {
		System.out.println(code);
	}
}
