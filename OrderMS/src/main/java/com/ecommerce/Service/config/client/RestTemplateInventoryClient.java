package com.ecommerce.Service.config.client;

import com.ecommerce.Service.dto.InventoryRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class RestTemplateInventoryClient {

    @Autowired
    private RestTemplate restTemplate;

    public boolean reserveStock(List<InventoryRequestDTO> request) {

        try {
            String url = "http://localhost:8083/inventory/reserve";

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (HttpClientErrorException e) {
            throw e;
        }
    }
}