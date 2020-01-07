package com.prova.liska.relatorio.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDto {
	
	private Long id;
	
	private String cnpj;
	
	private String nome;
	
	private String areaNegocio;
}