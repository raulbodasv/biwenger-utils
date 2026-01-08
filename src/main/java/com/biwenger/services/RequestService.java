package com.biwenger.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RequestService {
	
	@Autowired
	private WebClient webClient;
	
	private String urlBase = "https://biwenger.as.com/api/v2/";

	// Obtengo la info para poder crear los headers
    public String get_account_info(String token) {
    	return webClient.get()
                .uri(urlBase + "account")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header("X-Lang", "es")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String get_all_players_in_league(HttpHeaders headers) {
    	return webClient.get()
                .uri(urlBase + "competitions/la-liga/data?lang=es&score=5")
                .headers(h -> h.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    public String get_info_users_in_league(HttpHeaders headers) {
    	return webClient.get()
                .uri(urlBase + "league?include=all,-lastAccess&fields=*,standings,tournaments,group,settings(description)")
                .headers(h -> h.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    @Cacheable(value = "users", key = "#userId")
    public String get_template_players_in_league(HttpHeaders headers, String userId) {
    	String uri = String.format(urlBase + "user/%s?fields=*,account(id),players(id,owner),lineups(round,points,count,position),league(id,name,competition,type,mode,marketMode,scoreID),market,seasons,offers,lastPositions", userId);
    	return webClient.get()
                .uri(uri)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    @Cacheable(value = "players", key = "#playerId")
    public String get_player_name_by_id(HttpHeaders headers, String playerId) {
    	String uri = String.format(urlBase + "players/la-liga/%s", playerId);
    	return webClient.get()
                .uri(uri)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
	
}
