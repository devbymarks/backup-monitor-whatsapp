package br.com.monitorbackup.service;

import br.com.monitorbackup.model.BackupStatus;
import br.com.monitorbackup.model.Cliente;
import br.com.monitorbackup.model.StatusBackup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BackupScannerServiceTest {

    private static final Instant AGORA = Instant.parse("2026-09-11T22:00:00Z");
    private static final Clock CLOCK = Clock.fixed(AGORA, ZoneId.of("America/Sao_Paulo"));

    @TempDir
    Path tempDir;

    @Test
    void deveConsiderarApenasArquivoQueCasaComPadrao() throws Exception {
        Path auxiliar = tempDir.resolve("sqlhosts");
        Files.write(auxiliar, "x".getBytes(StandardCharsets.UTF_8));
        Files.setLastModifiedTime(auxiliar, FileTime.from(AGORA.minusSeconds(3600)));

        Path backup = tempDir.resolve("backup_0_L0");
        Files.write(backup, "backup".getBytes(StandardCharsets.UTF_8));
        Files.setLastModifiedTime(backup, FileTime.from(AGORA.minusSeconds(10 * 3600)));

        Cliente cliente = new Cliente("cliente-a", tempDir, "*_0_L0");
        BackupScannerService service = new BackupScannerService(Collections.singletonList(cliente), CLOCK, 24);

        BackupStatus status = service.verificarTodos().get(0);
        assertEquals(StatusBackup.OK, status.getStatus());
        assertEquals(Long.valueOf(10), status.getHorasSemBackup());
    }

    @Test
    void deveMarcarAtrasadoCom24HorasOuMais() throws Exception {
        Path backup = tempDir.resolve("backup_0_L0");
        Files.write(backup, "backup".getBytes(StandardCharsets.UTF_8));
        Files.setLastModifiedTime(backup, FileTime.from(AGORA.minusSeconds(24 * 3600)));

        Cliente cliente = new Cliente("cliente-b", tempDir, "*_0_L0");
        BackupScannerService service = new BackupScannerService(Collections.singletonList(cliente), CLOCK, 24);

        BackupStatus status = service.verificarTodos().get(0);
        assertEquals(StatusBackup.ATRASADO, status.getStatus());
        assertEquals(Long.valueOf(24), status.getHorasSemBackup());
    }

    @Test
    void deveMarcarSemBackupQuandoNaoHaArquivoValido() throws Exception {
        Files.write(tempDir.resolve("arquivo-configuracao.txt"), "x".getBytes(StandardCharsets.UTF_8));
        Cliente cliente = new Cliente("cliente-d", tempDir, "*_0_L0");
        BackupScannerService service = new BackupScannerService(Collections.singletonList(cliente), CLOCK, 24);

        BackupStatus status = service.verificarTodos().get(0);
        assertEquals(StatusBackup.SEM_BACKUP, status.getStatus());
        assertNull(status.getUltimoBackup());
        assertNull(status.getHorasSemBackup());
    }
}
