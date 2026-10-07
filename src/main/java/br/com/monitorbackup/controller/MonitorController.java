package br.com.monitorbackup.controller;

import br.com.monitorbackup.model.BackupStatus;
import br.com.monitorbackup.model.MonitoramentoResultado;
import br.com.monitorbackup.service.BackupScannerService;
import br.com.monitorbackup.service.MonitorService;
import br.com.monitorbackup.service.WhatsAppException;
import br.com.monitorbackup.service.WhatsAppService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MonitorController {

    private final BackupScannerService scannerService;
    private final MonitorService monitorService;
    private final WhatsAppService whatsAppService;

    public MonitorController(BackupScannerService scannerService,
                             MonitorService monitorService,
                             WhatsAppService whatsAppService) {
        this.scannerService = scannerService;
        this.monitorService = monitorService;
        this.whatsAppService = whatsAppService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Collections.singletonMap("status", "ok");
    }

    @GetMapping("/status")
    public List<BackupStatus> status() {
        return scannerService.verificarTodos();
    }

    @GetMapping("/status/{cliente}")
    public ResponseEntity<?> statusCliente(@PathVariable String cliente) {
        Optional<BackupStatus> status = scannerService.verificarPorNome(cliente);
        if (!status.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("erro", "Cliente não configurado"));
        }
        return ResponseEntity.ok(status.get());
    }

    @PostMapping("/teste-whatsapp")
    public ResponseEntity<Map<String, String>> testeWhatsApp() {
        Map<String, String> resposta = new LinkedHashMap<String, String>();
        try {
            whatsAppService.enviarMensagem("✅ Teste do Monitor de Backup.\n\nWhatsApp configurado com sucesso.");
            resposta.put("status", "ok");
            resposta.put("mensagem", "Mensagem de teste enviada");
            return ResponseEntity.ok(resposta);
        } catch (WhatsAppException e) {
            resposta.put("status", "erro");
            resposta.put("mensagem", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(resposta);
        }
    }

    @PostMapping("/monitorar")
    public MonitoramentoResultado monitorar() {
        return monitorService.monitorarEAlertar();
    }
}
