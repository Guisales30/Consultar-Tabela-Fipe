package br.com.guilherme.consultaFipe.principal;

import br.com.guilherme.consultaFipe.model.Dados;
import br.com.guilherme.consultaFipe.model.Modelos;
import br.com.guilherme.consultaFipe.model.Veiculo;
import br.com.guilherme.consultaFipe.service.ConsumoApi;
import br.com.guilherme.consultaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    Scanner in = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        var menu = """
                ***opções***
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar:
                """;

        System.out.println(menu);
        var opcao = in.nextLine();
        String endereco;

        if (opcao.toLowerCase().contains("car")){
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("moto")){
            endereco = URL_BASE + "motos/marcas";
        }else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoApi.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream().sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta: ");
        var codigoMarca = in.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);
        System.out.println("\nmodelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado: ");
        var nomeVeiculo = in.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos Filtrados:");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo para buscar os valores: ");
        var codigoModelo = in.nextLine();
        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();
        for (int i = 0; i< anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }
        System.out.println("\nTodos os veículos filtrados por ano");
        veiculos.forEach(System.out::println);

    }
}

