package br.com.monitorbackup.model;

import java.nio.file.Path;

public class Cliente {

    private final String nome;
    private final Path diretorio;
    private final String padraoBackup;

    public Cliente(String nome, Path diretorio, String padraoBackup) {
        this.nome = nome;
        this.diretorio = diretorio;
        this.padraoBackup = padraoBackup;
    }

    public String getNome() {
        return nome;
    }

    public Path getDiretorio() {
        return diretorio;
    }

    public String getPadraoBackup() {
        return padraoBackup;
    }
}
