package com.marcosolina.wateringcan.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller
 * 
 * @author Marco
 *
 */
@Controller
public class MainController {
	 @GetMapping(value = "/")
	    public String mainController() {
	        return "index";
	    }
}
