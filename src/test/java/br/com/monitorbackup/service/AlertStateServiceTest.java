package br.com.monitorbackup.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertStateServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void estadoDevePersistirNoDisco() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        Path arquivo = tempDir.resolve("alerta-diario.dat");

        AlertStateService primeiro = new AlertStateService(arquivo.toString(), clock);
        assertFalse(primeiro.jaEnviadoHoje());
        primeiro.registrarEnvioHoje();
        assertTrue(primeiro.jaEnviadoHoje());

        AlertStateService aposRestart = new AlertStateService(arquivo.toString(), clock);
        assertTrue(aposRestart.jaEnviadoHoje());
    }

    @Test
    void deveLiberarNovoAlertaNoDiaSeguinte() throws Exception {
        Path arquivo = tempDir.resolve("alerta-diario.dat");
        Clock diaUm = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        AlertStateService serviceDiaUm = new AlertStateService(arquivo.toString(), diaUm);
        serviceDiaUm.registrarEnvioHoje();

        Clock diaDois = Clock.fixed(Instant.parse("2026-09-12T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        AlertStateService serviceDiaDois = new AlertStateService(arquivo.toString(), diaDois);
        assertFalse(serviceDiaDois.jaEnviadoHoje());
    }
}
