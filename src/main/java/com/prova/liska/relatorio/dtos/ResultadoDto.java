package com.prova.liska.relatorio.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoDto {
	
	private String qtdeClientes;
	
	private String qtdeVendedores;
	
	private String idVendaCara;
	
	private String piorVendedor;
	
	@Override
	public String toString() {
		return qtdeClientes + "ç" + qtdeVendedores + "ç" + idVendaCara + "ç" + piorVendedor;
	}
}
