package com.enocta.api;

import com.enocta.config.ConfigReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Başarılı API yanıtlarının gövdesini okunabilir (pretty-print) JSON dosyası olarak kaydeder.
 * Dosya adına zaman damgası ve thread id eklenir; böylece paralel koşumlarda dosyalar çakışmaz.
 */
public class ResponseFileWriter {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Path outputDirectory = Path.of(ConfigReader.get("api.response.dir"));

    public Path write(String endpointName, Response response) {
        String fileName = String.format("%s_%s_t%d.json",
                endpointName, LocalDateTime.now().format(TIMESTAMP), Thread.currentThread().getId());
        Path file = outputDirectory.resolve(fileName);
        try {
            Files.createDirectories(outputDirectory);
            Files.writeString(file, prettyPrint(response.asString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Yanıt dosyaya yazılamadı: " + file, e);
        }
        return file;
    }

    private static String prettyPrint(String body) throws IOException {
        JsonNode json = MAPPER.readTree(body);
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    }
}
