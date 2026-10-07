package br.com.monitorbackup.service;

import br.com.monitorbackup.model.BackupStatus;
import br.com.monitorbackup.model.MonitoramentoResultado;
import br.com.monitorbackup.model.StatusBackup;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {

    private static final Logger log = LoggerFactory.getLogger(MonitorService.class);
    private static final DateTimeFormatter ALERTA_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BackupScannerService scannerService;
    private final AlertStateService alertStateService;
    private final WhatsAppService whatsAppService;
    private final Clock clock;

    public MonitorService(BackupScannerService scannerService,
                          AlertStateService alertStateService,
                          WhatsAppService whatsAppService,
                          Clock clock) {
        this.scannerService = scannerService;
        this.alertStateService = alertStateService;
        this.whatsAppService = whatsAppService;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${monitor.interval-ms:1800000}",
               initialDelayString = "${monitor.initial-delay-ms:60000}")
    public void executarAgendado() {
        try {
            monitorarEAlertar();
        } catch (Exception e) {
            log.error("[ERRO] Falha inesperada no ciclo de monitoramento: {}", e.getMessage(), e);
        }
    }

    public synchronized MonitoramentoResultado monitorarEAlertar() {
        List<BackupStatus> statuses = scannerService.verificarTodos();
        List<BackupStatus> problemas = new ArrayList<BackupStatus>();
        for (BackupStatus status : statuses) {
            if (status.requerAlerta()) {
                problemas.add(status);
            }
        }

        boolean jaEnviadoHoje = alertStateService.jaEnviadoHoje();
        boolean alertaEnviado = false;

        if (!problemas.isEmpty() && !jaEnviadoHoje) {
            String mensagem = montarMensagemAlerta(problemas);
            try {
                // Reserva persistentemente o alerta do dia ANTES da chamada externa.
                // Assim, mesmo em caso de timeout/resposta ambigua da Meta, o sistema
                // nao corre o risco de disparar uma segunda mensagem no mesmo dia.
                alertStateService.registrarEnvioHoje();
                jaEnviadoHoje = true;
                whatsAppService.enviarMensagem(mensagem);
                alertaEnviado = true;
                log.info("[ALERTA] Mensagem diaria enviada com {} cliente(s) em atraso/sem backup", problemas.size());
            } catch (IOException e) {
                log.error("[ERRO] Nao foi possivel reservar/persistir o alerta diario. WhatsApp nao sera chamado: {}", e.getMessage());
            } catch (WhatsAppException e) {
                log.error("[ERRO] Erro ao enviar WhatsApp. A tentativa do dia permanece registrada para evitar duplicidade: {}", e.getMessage());
            }
        } else if (!problemas.isEmpty()) {
            log.info("[ALERTA] Alerta diario ja enviado hoje. Nenhuma nova mensagem sera enviada neste dia.");
        }

        return new MonitoramentoResultado(statuses, problemas.size(), alertaEnviado, jaEnviadoHoje);
    }

    String montarMensagemAlerta(List<BackupStatus> problemas) {
        StringBuilder sb = new StringBuilder();
        sb.append("🚨 ALERTA DE BACKUPS\n\n");
        sb.append("Os seguintes clientes estão com backup atrasado ou sem backup válido:\n\n");

        for (BackupStatus status : problemas) {
            sb.append("🔴 ").append(status.getCliente().toUpperCase(Locale.ROOT)).append("\n");
            if (status.getStatus() == StatusBackup.SEM_BACKUP) {
                sb.append("Último backup: não encontrado\n");
                sb.append("Status: SEM BACKUP\n\n");
            } else {
                sb.append("Último backup: ").append(status.getUltimoBackup()).append("\n");
                sb.append("Atraso: ").append(status.getHorasSemBackup()).append(" horas\n\n");
            }
        }

        sb.append("Total de clientes com problema: ").append(problemas.size()).append("\n\n");
        sb.append("⚠️ Verificar os servidores dos clientes.\n");
        sb.append("Monitoramento: ").append(ALERTA_DATA.format(LocalDateTime.now(clock)));
        return sb.toString();
    }
}
