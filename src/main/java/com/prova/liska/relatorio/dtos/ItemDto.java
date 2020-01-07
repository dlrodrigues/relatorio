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
public class ItemDto {
	
	private Long id;
	
	private Long quantidade;
	
	private BigDecimal preco;
}
