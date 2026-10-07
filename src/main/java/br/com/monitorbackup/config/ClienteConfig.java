package br.com.monitorbackup.config;

import br.com.monitorbackup.model.Cliente;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class ClienteConfig {

    @Bean
    public List<Cliente> clientes(
            @Value("${monitor.base-dir:/backup/clientes}") String baseDir,
            @Value("${monitor.clients:}") String clientsConfig) {

        List<Cliente> clientes = new ArrayList<Cliente>();

        if (!StringUtils.hasText(clientsConfig)) {
            return Collections.unmodifiableList(clientes);
        }

        String[] entries = clientsConfig.split(",");
        for (String entry : entries) {
            String value = entry.trim();
            if (!StringUtils.hasText(value)) {
                continue;
            }

            String[] parts = value.split(":", 2);
            if (parts.length != 2
                    || !StringUtils.hasText(parts[0])
                    || !StringUtils.hasText(parts[1])) {
                throw new IllegalArgumentException(
                        "Entrada invalida em monitor.clients. Use nome:padrao-do-backup");
            }

            String nome = parts[0].trim();
            String padrao = parts[1].trim();
            Path diretorio = Paths.get(baseDir, nome);

            clientes.add(new Cliente(nome, diretorio, padrao));
        }

        return Collections.unmodifiableList(clientes);
    }
}
