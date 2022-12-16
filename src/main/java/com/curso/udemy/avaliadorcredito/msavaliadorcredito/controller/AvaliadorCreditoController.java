package com.curso.udemy.avaliadorcredito.msavaliadorcredito.controller;

import com.curso.udemy.avaliadorcredito.msavaliadorcredito.application.ex.DadosClientenotFoundException;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.domain.*;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.service.AvalidorCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvalidorCreditoService avalidorCreditoService;

    @GetMapping
    public String status(){
        return "app avaliador credito ok";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity consultarSituacaoCliente(@RequestParam("cpf") String cpf ) {
        try {
            SituacaoCliente situacaoCliente = avalidorCreditoService.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situacaoCliente);
        }catch (DadosClientenotFoundException e){
           return ResponseEntity.notFound().build();
        }catch (ErroComunicacaoMicroservicesException e){
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }

    }

    @PostMapping
    public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dados){
        try {
           RetornoAvaliacaoCliente retornoAvaliacaoCliente = avalidorCreditoService.realizarAvaliacao(dados.getCpf(), dados.getRenda());
            return ResponseEntity.ok(retornoAvaliacaoCliente);
        }catch (DadosClientenotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (ErroComunicacaoMicroservicesException e){
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity solicitarCartao(@RequestBody DadosSolicitacaoEmissaoCartao dados){
        try {
            ProtocoloSolicitacaoCartao protocoloSolicitacaoCartao = avalidorCreditoService.solicitacaoEmissaoCartao(dados);
            return ResponseEntity.ok(protocoloSolicitacaoCartao);
        }catch (ErroSolicitacaoCartaoException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
