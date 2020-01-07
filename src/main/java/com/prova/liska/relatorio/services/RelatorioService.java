package com.prova.liska.relatorio.services;

import com.prova.liska.relatorio.dtos.ClienteDto;
import com.prova.liska.relatorio.dtos.ItemDto;
import com.prova.liska.relatorio.dtos.ResultadoDto;
import com.prova.liska.relatorio.dtos.VendaDto;
import com.prova.liska.relatorio.dtos.VendedorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Slf4j
@Service
public class RelatorioService {
	
	private String path = System.getProperty("user.dir") + "/data";
	
	public void relatorio() throws Exception {
		log.info("Inicializando listener da pastade entrada.");
		
		WatchService watchService = FileSystems.getDefault().newWatchService();
		
		Path dir = Paths.get(path + "/in");
		
		try {
			dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		processamento(watchService, dir);
	}
	
	private void processamento(WatchService watchService, Path dir) {
		for (; ; ) {
			WatchKey key;
			try {
				key = watchService.take();
			} catch (InterruptedException x) {
				break;
			}
			
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();
				
				if (kind == ENTRY_CREATE) {
					log.info("Novo Arquivo criado: {}", filename);
					processarInformacoes(filename);
				}
			}
			
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
	}
	
	private void processarInformacoes(Path filename) {
		List<VendedorDto> vendedores = new ArrayList<>();
		List<ClienteDto> clientes = new ArrayList<>();
		List<VendaDto> vendas = new ArrayList<>();
		
		try {
			List<String> linhasArquivo = Files.readAllLines(Paths.get(path + "/in/" + filename.getFileName().toString()));
			if (!linhasArquivo.isEmpty()) {
				linhasArquivo.forEach(linha -> {
					if (linha.indexOf("001ç") == 0) {
						vendedores.add(this.createVendedor(linha));
					} else if (linha.indexOf("002ç") == 0) {
						clientes.add(createCliente(linha));
					} else if (linha.indexOf("003ç") == 0) {
						vendas.add(createVenda(linha));
					}
				});
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		ResultadoDto resultadoDto = new ResultadoDto();
		resultadoDto.setQtdeClientes(String.valueOf(clientes.size()));
		resultadoDto.setQtdeVendedores(String.valueOf(vendedores.size()));
		
		final VendaDto vendaDto = new VendaDto(null, null, new ArrayList<>(), null);
		vendaDto.getItens().add(new ItemDto(null, null, BigDecimal.ZERO));
		
		vendas.forEach(venda -> {
			Double vendaAtual = vendaDto.getItens().stream().map(ItemDto::getPreco).reduce(BigDecimal::add).get().doubleValue();
			Double novaVenda = venda.getItens().stream().map(ItemDto::getPreco).reduce(BigDecimal::add).get().doubleValue();
			
			if (vendaAtual < novaVenda) {
				vendaDto.setId(venda.getId());
				vendaDto.setIdVenda(venda.getIdVenda());
				vendaDto.setItens(venda.getItens());
				vendaDto.setVendedor(venda.getVendedor());
			}
		});
		
		resultadoDto.setIdVendaCara(String.valueOf(vendaDto.getIdVenda()));
		
		vendas.forEach(venda -> {
			Double vendaAtual = vendaDto.getItens().stream().map(ItemDto::getPreco).reduce(BigDecimal::add).get().doubleValue();
			Double novaVenda = venda.getItens().stream().map(ItemDto::getPreco).reduce(BigDecimal::add).get().doubleValue();
			
			if (vendaAtual > novaVenda) {
				vendaDto.setId(venda.getId());
				vendaDto.setIdVenda(venda.getIdVenda());
				vendaDto.setItens(venda.getItens());
				vendaDto.setVendedor(venda.getVendedor());
			}
		});
		
		resultadoDto.setPiorVendedor(vendaDto.getVendedor());
		
		gerarArquivoSaida(resultadoDto, filename.getFileName().toString());
		log.info("Arquivo gerado.");
	}
	
	private VendedorDto createVendedor(String linha) {
		String[] linhaSeparada = linha.split("ç");
		
		return new VendedorDto(Long.valueOf(linhaSeparada[0]), linhaSeparada[1], linhaSeparada[2], new BigDecimal(linhaSeparada[3]));
	}
	
	private ClienteDto createCliente(String linha) {
		String[] linhaSeparada = linha.split("ç");
		
		return new ClienteDto(Long.valueOf(linhaSeparada[0]), linhaSeparada[1], linhaSeparada[2], linhaSeparada[3]);
	}
	
	private VendaDto createVenda(String linha) {
		String[] linhaSeparada = linha.split("ç");
		
		VendaDto venda = new VendaDto(Long.valueOf(linhaSeparada[0]), Long.valueOf(linhaSeparada[1]), new ArrayList<>(), linhaSeparada[3]);
		String linhaItem = linhaSeparada[2].substring(1, linhaSeparada[2].length() - 1);
		String[] itens = linhaItem.split(",");
		Arrays.stream(itens).forEach(item -> {
			String[] itemSeparado = item.split("-");
			venda.getItens().add(new ItemDto(Long.valueOf(itemSeparado[0]), Long.valueOf(itemSeparado[1]), new BigDecimal(itemSeparado[2])));
		});
		
		return venda;
	}
	
	private void gerarArquivoSaida(ResultadoDto resultadoDto, String nomeArquivo) {
		log.debug(resultadoDto.toString());
		
		nomeArquivo = "saida_" + System.currentTimeMillis() + "_" + nomeArquivo;
		Path p = Paths.get(path + "/out/" + nomeArquivo);
		try {
			Files.write(p, resultadoDto.toString().getBytes());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
