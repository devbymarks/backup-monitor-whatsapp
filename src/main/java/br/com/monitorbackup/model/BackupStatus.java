package br.com.monitorbackup.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class BackupStatus {

    private final String cliente;
    private final String ultimoBackup;
    private final Long horasSemBackup;
    private final StatusBackup status;

    public BackupStatus(String cliente, String ultimoBackup, Long horasSemBackup, StatusBackup status) {
        this.cliente = cliente;
        this.ultimoBackup = ultimoBackup;
        this.horasSemBackup = horasSemBackup;
        this.status = status;
    }

    public String getCliente() {
        return cliente;
    }

    public String getUltimoBackup() {
        return ultimoBackup;
    }

    public Long getHorasSemBackup() {
        return horasSemBackup;
    }

    public StatusBackup getStatus() {
        return status;
    }

    public boolean requerAlerta() {
        return status == StatusBackup.ATRASADO || status == StatusBackup.SEM_BACKUP;
    }
}
