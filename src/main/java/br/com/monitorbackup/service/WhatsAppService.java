package br.com.monitorbackup.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

    private final RestTemplate restTemplate;
    private final String token;
    private final String phoneNumberId;
    private final String destinatario;
    private final String graphVersion;
    private final String messageMode;
    private final String templateName;
    private final String templateLanguage;

    public WhatsAppService(RestTemplate restTemplate,
                           @Value("${whatsapp.token:}") String token,
                           @Value("${whatsapp.phone-number-id:}") String phoneNumberId,
                           @Value("${whatsapp.to:}") String destinatario,
                           @Value("${whatsapp.graph-version:v23.0}") String graphVersion,
                           @Value("${whatsapp.message-mode:text}") String messageMode,
                           @Value("${whatsapp.template-name:}") String templateName,
                           @Value("${whatsapp.template-language:pt_BR}") String templateLanguage) {
        this.restTemplate = restTemplate;
        this.token = token;
        this.phoneNumberId = phoneNumberId;
        this.destinatario = destinatario;
        this.graphVersion = graphVersion;
        this.messageMode = messageMode;
        this.templateName = templateName;
        this.templateLanguage = templateLanguage;
    }

    public void enviarMensagem(String mensagem) {
        validarConfiguracao();

        String url = "https://graph.facebook.com/" + graphVersion + "/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> payload;
        if ("template".equalsIgnoreCase(messageMode)) {
            payload = montarPayloadTemplate(mensagem);
        } else {
            payload = montarPayloadTexto(mensagem);
        }

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<Map<String, Object>>(payload, headers),
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new WhatsAppException("WhatsApp Cloud API retornou HTTP " + response.getStatusCodeValue());
            }
        } catch (HttpStatusCodeException e) {
            throw new WhatsAppException("WhatsApp Cloud API retornou HTTP " + e.getRawStatusCode(), e);
        } catch (WhatsAppException e) {
            throw e;
        } catch (Exception e) {
            throw new WhatsAppException("Falha de comunicacao com a WhatsApp Cloud API", e);
        }
    }

    private Map<String, Object> montarPayloadTexto(String mensagem) {
        Map<String, Object> text = new LinkedHashMap<String, Object>();
        text.put("preview_url", false);
        text.put("body", mensagem);

        Map<String, Object> payload = basePayload();
        payload.put("type", "text");
        payload.put("text", text);
        return payload;
    }

    private Map<String, Object> montarPayloadTemplate(String mensagem) {
        Map<String, Object> language = new LinkedHashMap<String, Object>();
        language.put("code", templateLanguage);

        Map<String, Object> parameter = new LinkedHashMap<String, Object>();
        parameter.put("type", "text");
        parameter.put("text", mensagem);

        List<Map<String, Object>> parameters = new ArrayList<Map<String, Object>>();
        parameters.add(parameter);

        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("type", "body");
        body.put("parameters", parameters);

        List<Map<String, Object>> components = new ArrayList<Map<String, Object>>();
        components.add(body);

        Map<String, Object> template = new LinkedHashMap<String, Object>();
        template.put("name", templateName);
        template.put("language", language);
        template.put("components", components);

        Map<String, Object> payload = basePayload();
        payload.put("type", "template");
        payload.put("template", template);
        return payload;
    }

    private Map<String, Object> basePayload() {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", destinatario);
        return payload;
    }

    private void validarConfiguracao() {
        if (!StringUtils.hasText(token)
                || !StringUtils.hasText(phoneNumberId)
                || !StringUtils.hasText(destinatario)
                || !StringUtils.hasText(graphVersion)) {
            log.error("[ERRO] Configuracao do WhatsApp incompleta. Verifique token, phone-number-id, to e graph-version.");
            throw new WhatsAppException("Configuracao do WhatsApp incompleta");
        }

        if (!"text".equalsIgnoreCase(messageMode) && !"template".equalsIgnoreCase(messageMode)) {
            throw new WhatsAppException("whatsapp.message-mode deve ser 'text' ou 'template'");
        }

        if ("template".equalsIgnoreCase(messageMode)
                && (!StringUtils.hasText(templateName) || !StringUtils.hasText(templateLanguage))) {
            log.error("[ERRO] Modo template habilitado, mas nome/idioma do template nao foram configurados.");
            throw new WhatsAppException("Configuracao do template do WhatsApp incompleta");
        }
    }
}
