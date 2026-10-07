package br.com.monitorbackup.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AlertStateService {

    private static final Logger log = LoggerFactory.getLogger(AlertStateService.class);

    private final Path stateFile;
    private final Clock clock;

    public AlertStateService(@Value("${monitor.state-file:/opt/monitor-backup/data/alerta-diario.dat}") String stateFile,
                             Clock clock) {
        this.stateFile = Paths.get(stateFile);
        this.clock = clock;
    }

    public synchronized boolean jaEnviadoHoje() {
        LocalDate hoje = LocalDate.now(clock);
        if (!Files.exists(stateFile)) {
            return false;
        }

        try {
            String conteudo = new String(Files.readAllBytes(stateFile), StandardCharsets.UTF_8).trim();
            if (conteudo.isEmpty()) {
                return false;
            }
            return hoje.equals(LocalDate.parse(conteudo));
        } catch (Exception e) {
            log.error("[ERRO] Nao foi possivel ler o estado do alerta diario: {}", e.getMessage());
            return false;
        }
    }

    public synchronized void registrarEnvioHoje() throws IOException {
        LocalDate hoje = LocalDate.now(clock);
        Path parent = stateFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path temporario = stateFile.resolveSibling(stateFile.getFileName().toString() + ".tmp");
        Files.write(temporario, hoje.toString().getBytes(StandardCharsets.UTF_8));

        try {
            Files.move(temporario, stateFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporario, stateFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
