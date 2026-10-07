package br.com.monitorbackup.model;

import java.util.List;

public class MonitoramentoResultado {

    private final List<BackupStatus> clientes;
    private final int totalProblemas;
    private final boolean alertaEnviado;
    private final boolean alertaJaEnviadoHoje;

    public MonitoramentoResultado(List<BackupStatus> clientes,
                                  int totalProblemas,
                                  boolean alertaEnviado,
                                  boolean alertaJaEnviadoHoje) {
        this.clientes = clientes;
        this.totalProblemas = totalProblemas;
        this.alertaEnviado = alertaEnviado;
        this.alertaJaEnviadoHoje = alertaJaEnviadoHoje;
    }

    public List<BackupStatus> getClientes() {
        return clientes;
    }

    public int getTotalProblemas() {
        return totalProblemas;
    }

    public boolean isAlertaEnviado() {
        return alertaEnviado;
    }

    public boolean isAlertaJaEnviadoHoje() {
        return alertaJaEnviadoHoje;
    }
}
