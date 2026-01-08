package com.biwenger.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.biwenger.model.PlayerDto;
import com.biwenger.services.RequestService;
import com.biwenger.services.UtilService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;

@Controller
public class IndexController {
	
	@Autowired
	private RequestService request;
	
	private final UtilService util;

	private HttpHeaders headers;
	
	private String loginUrl = "https://biwenger.as.com/api/v2/auth/login";

    @Value("${auth.user}")
    private String user;

    @Value("${auth.password}")
    private String password;
    
    @Value("${info.league}")
    private String league;

    public IndexController(UtilService util) {
	    this.util = util;
	}
    
    @PostConstruct
    public void init() {
        // Se ejecuta después de que Spring inyecte todo
        this.headers = util.getHeaders(user, password, loginUrl, league);
    }

	@GetMapping("/")
	public String index(Model model) {
		String a = request.get_all_players_in_league(headers);
		System.out.println(headers.toString());
		model.addAttribute("title", "Biwenger Utils");
		model.addAttribute("token", a);
		return "index";
	}
    
	// Obtener proximas clausulas
    @GetMapping("/clauses")
	public String getNextClauses(Model model) {
		String infoUsersInLeague = request.get_info_users_in_league(headers);
		List<PlayerDto> players = new ArrayList<>();
		
		JsonNode json = util.converterToJson(infoUsersInLeague);
		for (JsonNode u : json.path("data").path("standings")) {
			
			String template = request.get_template_players_in_league(headers, u.path("id").asText());
			JsonNode jsonPlayersForUser = util.converterToJson(template).path("data");
			System.out.println(jsonPlayersForUser.toString());
			for (JsonNode p : jsonPlayersForUser.path("players")) {
				String playerJson = request.get_player_name_by_id(headers, p.path("id").asText());
				String playerName = util.converterToJson(playerJson).path("data").path("name").asText();

				PlayerDto player = new PlayerDto(
				        playerName,
				        jsonPlayersForUser.path("name").asText(),
				        util.numberFormatter(p.path("owner").path("clause").asText(), true),
				        util.converterTimestampToDate(p.path("owner").path("clauseLockedUntil").asText()),
				        util.numberFormatter(p.path("owner").path("invested").asText(), true),
				        util.converterTimestampToDate(p.path("owner").path("date").asText()),
				        util.numberFormatter(p.path("owner").path("price").asText(), true)
				);
				players.add(player);
				System.out.println(player.toString());
			}
		}
		
		players.sort(
			    Comparator.comparing(
			        PlayerDto::clauseLockedUntil,
			        Comparator.nullsFirst(Comparator.naturalOrder())
			    ).reversed()
			);

		model.addAttribute("title", "Biwenger Utils");
		model.addAttribute("players", players);
		return "clauses";
	}
	
}

