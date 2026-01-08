package com.biwenger.services;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LoginService {

	private final WebClient webClient;

    public LoginService(WebClient webClient) {
        this.webClient = webClient;
    }
    
    // Obtengo el token
	public String getToken(String user, String password, String loginUrl) {
	    System.out.println("Login process");
	    Map<String, String> data = Map.of(
	            "email", user,
	            "password", password
	    );

	    Map<String, Object> body = webClient.post()
	            .uri(loginUrl)
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .bodyValue(data)
	            .retrieve()
	            .bodyToMono(Map.class)
	            .block();

	    System.out.println("contents: " + body);

	    if (body != null && body.containsKey("token")) {
	        System.out.println("call login ok!");
	        return body.get("token").toString();
	    } else {
	        Object status = body != null ? body.get("status") : "unknown";
	        System.out.println("error in login call, status: " + status);
	        return "error, status " + status;
	    }
	}
}
