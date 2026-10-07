package br.com.monitorbackup.service;

import br.com.monitorbackup.model.BackupStatus;
import br.com.monitorbackup.model.Cliente;
import br.com.monitorbackup.model.StatusBackup;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.FileSystems;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BackupScannerService {

    private static final Logger log = LoggerFactory.getLogger(BackupScannerService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<Cliente> clientes;
    private final Clock clock;
    private final long limiteHoras;

    public BackupScannerService(List<Cliente> clientes,
                                Clock clock,
                                @Value("${monitor.limit-hours:24}") long limiteHoras) {
        this.clientes = clientes;
        this.clock = clock;
        this.limiteHoras = limiteHoras;
    }

    public List<BackupStatus> verificarTodos() {
        return clientes.stream()
                .map(this::verificarClienteSeguro)
                .collect(Collectors.toList());
    }

    public Optional<BackupStatus> verificarPorNome(String nome) {
        return clientes.stream()
                .filter(cliente -> cliente.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .map(this::verificarClienteSeguro);
    }

    BackupStatus verificarClienteSeguro(Cliente cliente) {
        try {
            return verificarCliente(cliente);
        } catch (Exception e) {
            log.error("[ERRO] {} - falha ao verificar diretorio de backup: {}",
                    cliente.getNome(), e.getMessage());
            return new BackupStatus(cliente.getNome(), null, null, StatusBackup.SEM_BACKUP);
        }
    }

    private BackupStatus verificarCliente(Cliente cliente) throws IOException {
        Path diretorio = cliente.getDiretorio();
        if (!Files.isDirectory(diretorio)) {
            throw new IOException("Diretorio inexistente ou indisponivel: " + diretorio);
        }

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + cliente.getPadraoBackup());
        Path arquivoMaisRecente = null;
        FileTime dataMaisRecente = null;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(diretorio)) {
            for (Path arquivo : stream) {
                if (!Files.isRegularFile(arquivo)) {
                    continue;
                }
                if (!matcher.matches(arquivo.getFileName())) {
                    continue;
                }

                FileTime modificacao = Files.getLastModifiedTime(arquivo);
                if (dataMaisRecente == null || modificacao.compareTo(dataMaisRecente) > 0) {
                    arquivoMaisRecente = arquivo;
                    dataMaisRecente = modificacao;
                }
            }
        }

        if (arquivoMaisRecente == null || dataMaisRecente == null) {
            log.warn("[SEM_BACKUP] {} - nenhum arquivo valido encontrado para o padrao {}",
                    formatarNome(cliente.getNome()), cliente.getPadraoBackup());
            return new BackupStatus(cliente.getNome(), null, null, StatusBackup.SEM_BACKUP);
        }

        Instant agora = clock.instant();
        Instant ultimoBackup = dataMaisRecente.toInstant();
        Duration semBackup = Duration.between(ultimoBackup, agora);
        if (semBackup.isNegative()) {
            semBackup = Duration.ZERO;
        }

        long horas = semBackup.toHours();
        boolean atrasado = semBackup.compareTo(Duration.ofHours(limiteHoras)) >= 0;
        String dataFormatada = FORMATTER.withZone(ZoneId.of(clock.getZone().getId())).format(ultimoBackup);

        if (atrasado) {
            log.warn("[ATRASADO] {} - ultimo backup: {} - {} horas sem backup",
                    formatarNome(cliente.getNome()), dataFormatada, horas);
            return new BackupStatus(cliente.getNome(), dataFormatada, horas, StatusBackup.ATRASADO);
        }

        log.info("[OK] {} - ultimo backup: {} - {} horas sem backup",
                formatarNome(cliente.getNome()), dataFormatada, horas);
        return new BackupStatus(cliente.getNome(), dataFormatada, horas, StatusBackup.OK);
    }

    private String formatarNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            return nome;
        }
        return nome.substring(0, 1).toUpperCase(Locale.ROOT) + nome.substring(1);
    }
}
