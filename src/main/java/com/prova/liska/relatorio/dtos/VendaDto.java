package com.prova.liska.relatorio.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VendaDto {
	
	private Long id;
	
	private Long idVenda;
	
	private List<ItemDto> itens;
	
	private String vendedor;
}
