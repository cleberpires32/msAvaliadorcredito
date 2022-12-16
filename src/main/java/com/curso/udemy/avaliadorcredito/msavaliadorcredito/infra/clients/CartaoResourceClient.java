package com.curso.udemy.avaliadorcredito.msavaliadorcredito.infra.clients;

import com.curso.udemy.avaliadorcredito.msavaliadorcredito.domain.Cartao;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.domain.CartaoCliente;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscartoes", path = "/cartoes")
public interface CartaoResourceClient {

    @GetMapping(params = "cpf")
    ResponseEntity<List<CartaoCliente>> getCartoesPorCliente(@RequestParam("cpf") String cpf);

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesComRendaAteh(@RequestParam Long renda);
}
