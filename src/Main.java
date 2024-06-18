import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Doacao {
    private String tipo;
    private double quantidade;
    private LocalDate data;
    private Doador doador;

    public Doacao(String tipo, double quantidade, LocalDate data, Doador doador) {
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.data = data;
        this.doador = doador;
    }

    public double getQuantidade() {
        return quantidade;
    }

    // Getters e Setters para outros campos, se necessário
}

class Doador {
    private String nome;
    private String contato;

    public Doador(String nome, String contato) {
        this.nome = nome;
        this.contato = contato;
    }

    // Getters e Setters para outros campos, se necessário
}

class GerenciadorDoacoes {
    private List<Doacao> listaDoacoes = new ArrayList<>();

    public void adicionarDoacao(Doacao doacao) {
        listaDoacoes.add(doacao);
    }

    public double calcularTotalDoacoes() {
        return listaDoacoes.stream().mapToDouble(Doacao::getQuantidade).sum();
    }
}

// Métodos para salvar e recuperar dados
