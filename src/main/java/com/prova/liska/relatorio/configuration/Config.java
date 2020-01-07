package com.prova.liska.relatorio.configuration;

import com.prova.liska.relatorio.services.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
	
	@Autowired
	private RelatorioService service;
	
	@Bean
	public void configuration() throws Exception {
		this.service.relatorio();
	}
}
