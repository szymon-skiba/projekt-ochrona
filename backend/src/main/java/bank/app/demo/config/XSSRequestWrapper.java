package bank.app.demo.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.owasp.encoder.Encode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.jsonwebtoken.io.IOException;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private byte[] rawData;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public XSSRequestWrapper(HttpServletRequest request) throws IOException, java.io.IOException {
        super(request);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(request.getInputStream().readAllBytes());
        this.rawData = baos.toByteArray();
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = sanitizeInput(values[i]);
        }
        return encodedValues;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException, java.io.IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);
        JsonNode jsonNode = objectMapper.readTree(byteArrayInputStream);
        sanitizeJsonNode(jsonNode);

        String sanitizedJson = objectMapper.writeValueAsString(jsonNode);
        ByteArrayInputStream sanitizedInput = new ByteArrayInputStream(sanitizedJson.getBytes());

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return sanitizedInput.read();
            }

            @Override
            public boolean isFinished() {
                return sanitizedInput.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }
        };
    }

    private void sanitizeJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fieldNames().forEachRemaining(fieldName -> {
                JsonNode childNode = objectNode.get(fieldName);
                if (childNode.isTextual()) {
                    objectNode.put(fieldName, Encode.forHtml(childNode.asText()));
                } else {
                    sanitizeJsonNode(childNode);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                sanitizeJsonNode(arrayNode.get(i));
            }
        }
    }

    private String sanitizeInput(String string) {
        return Encode.forHtml(string);
    }
}