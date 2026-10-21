package br.com.financepro.financePro.common.json;

import br.com.financepro.financePro.common.exceptions.FileException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FinancialJsonFileReader {

    private static final String path = "classpath:questions/questions-financial-profile.json";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    public RootFinancialData read() {
        Resource resource = resourceLoader.getResource(path);

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, RootFinancialData.class);
        }
        catch (Exception e) {
            throw new FileException("Error in parsing json");
        }
    }

}