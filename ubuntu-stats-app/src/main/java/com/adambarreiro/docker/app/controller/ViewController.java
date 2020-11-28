package com.adambarreiro.docker.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Renders the main app view.
 */
@Controller
public class ViewController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
}
