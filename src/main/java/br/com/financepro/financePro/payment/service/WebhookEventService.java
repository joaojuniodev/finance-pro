package br.com.financepro.financePro.payment.service;

import br.com.financepro.financePro.payment.repository.WebhookEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebhookEventService {

    @Autowired
    private WebhookEventRepository repository;


}