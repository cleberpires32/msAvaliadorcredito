package com.curso.udemy.avaliadorcredito.msavaliadorcredito.infra.clients;

import com.curso.udemy.avaliadorcredito.msavaliadorcredito.domain.DadosCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "msclientes", path = "/clientes")
public interface ClienteResourceClient {

    @GetMapping
    public String dadosCliente();
    @GetMapping(params = "cpf")
    ResponseEntity<DadosCliente> getCliente(@RequestParam("cpf") String cpf);
}
