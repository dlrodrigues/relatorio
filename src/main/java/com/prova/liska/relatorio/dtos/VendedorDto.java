package com.prova.liska.relatorio.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VendedorDto {
	
	private Long id;
	
	private String cpf;
	
	private String nome;
	
	private BigDecimal salario;
}
