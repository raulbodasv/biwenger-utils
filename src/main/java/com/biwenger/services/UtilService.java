package com.biwenger.services;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UtilService {
	
	private final LoginService loginServiceBeta;
	
	@Autowired
	private RequestService request;

	public UtilService(LoginService loginService) {
	    this.loginServiceBeta = loginService;
	}

	// Obtengo las cabeceras para realizar peticiones con la sesion iniciada
    public HttpHeaders getHeaders(String user, String password, String loginUrl, String league) {
        String token = loginServiceBeta.getToken(user, password, loginUrl);
        String response = request.get_account_info(token);
        HttpHeaders headers = new HttpHeaders();
		
		JsonNode json = converterToJson(response);
		System.out.println("League: "+league);
		for (JsonNode l : json.path("data").path("leagues")) {
			
			// Liga definida en el properties
		    if(league.contentEquals(l.path("name").asText())) {
		    	headers.setContentType(MediaType.APPLICATION_JSON);
		    	headers.setAccept(List.of(
		    	        MediaType.APPLICATION_JSON,
		    	        MediaType.TEXT_PLAIN
		    	));
		    	headers.add("X-Lang", "es");
		    	headers.add("X-League", l.path("id").asText());
		    	headers.add("X-User", l.path("user").path("id").asText());
		    	headers.setBearerAuth(token);
		    	return headers;
		    }
		}
		return headers;
    }
	
    // Convertir un String con formato JSON, a JsonNode
	public JsonNode converterToJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonConverter = null;
		try {
			jsonConverter = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			System.out.println("No tiene formato JSON");
			e.printStackTrace();
		}
		return jsonConverter;	
    }
	
	// Convertir un String con formato Timestamp, a LocalDateTime
	public LocalDateTime converterTimestampToDate(String timestamp) {
		if (!"".equals(timestamp)) {
			long epochSeconds = Long.parseLong(timestamp);
	
			LocalDateTime dateTime = LocalDateTime.ofInstant(
			        Instant.ofEpochSecond(epochSeconds),
			        ZoneId.systemDefault()
			);
			
			return dateTime;
			
		}
		return null;
    }
	
	// Formatear un numero a numero con separador de miles y simbolo de moneda
	public String numberFormatter(String number, boolean isPrice) {
		if (!"".equals(number)) {
			NumberFormat nf = NumberFormat.getInstance(new Locale("es", "ES"));
			String valorFormateado = nf.format(Long.parseLong(number));
			if(isPrice) {
				return valorFormateado + "€";
			}
			return valorFormateado;
		}
		return null;
    }
	
	
	
}
