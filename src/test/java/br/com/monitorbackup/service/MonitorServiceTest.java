package br.com.monitorbackup.service;

import br.com.monitorbackup.model.BackupStatus;
import br.com.monitorbackup.model.MonitoramentoResultado;
import br.com.monitorbackup.model.StatusBackup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonitorServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void deveEnviarUmaUnicaMensagemPorDiaComTodosOsProblemas() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        BackupScannerService scanner = mock(BackupScannerService.class);
        WhatsAppService whatsapp = mock(WhatsAppService.class);
        AlertStateService state = new AlertStateService(tempDir.resolve("estado.dat").toString(), clock);

        when(scanner.verificarTodos()).thenReturn(Arrays.asList(
                new BackupStatus("cliente-a", "10/09/2026 07:00", 26L, StatusBackup.ATRASADO),
                new BackupStatus("cliente-b", null, null, StatusBackup.SEM_BACKUP),
                new BackupStatus("cliente-c", "11/09/2026 06:00", 3L, StatusBackup.OK)
        ));

        MonitorService service = new MonitorService(scanner, state, whatsapp, clock);
        MonitoramentoResultado primeira = service.monitorarEAlertar();
        MonitoramentoResultado segunda = service.monitorarEAlertar();

        assertTrue(primeira.isAlertaEnviado());
        assertFalse(segunda.isAlertaEnviado());
        assertEquals(2, primeira.getTotalProblemas());
        verify(whatsapp, times(1)).enviarMensagem(org.mockito.ArgumentMatchers.anyString());
    }
}
