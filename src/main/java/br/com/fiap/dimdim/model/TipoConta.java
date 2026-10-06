package br.com.fiap.dimdim.model;

public enum TipoConta {
    CORRENTE("Corrente"),
    POUPANCA("Poupança"),
    SALARIO("Salário");

    private final String descricao;

    TipoConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
