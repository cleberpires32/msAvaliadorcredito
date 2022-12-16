package com.curso.udemy.avaliadorcredito.msavaliadorcredito.service;

import com.curso.udemy.avaliadorcredito.msavaliadorcredito.application.ex.DadosClientenotFoundException;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.domain.*;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.infra.clients.CartaoResourceClient;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.infra.clients.ClienteResourceClient;
import com.curso.udemy.avaliadorcredito.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvalidorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartaoResourceClient cartaoResourceClient;

    private final SolicitacaoEmissaoCartaoPublisher solicitacaoEmissaoCartaoPublisher;

    //private final SolicitacaoE
    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClientenotFoundException, ErroComunicacaoMicroservicesException {

        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.getCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartaoResourceClient.getCartoesPorCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClientenotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    };

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClientenotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.getCliente(cpf);
            ResponseEntity<List<Cartao>> cartaoResource = cartaoResourceClient.getCartoesComRendaAteh(renda);

            List<Cartao> cartoes = cartaoResource.getBody();

            var listaCartoesAprovados = cartoes.stream().map(c -> {
                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = c.getLimiteBasico();
                BigDecimal rendaBD = BigDecimal.valueOf(renda);
                BigDecimal idadeDB = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idadeDB.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setLimiteAprovado(limiteAprovado);
                aprovado.setCartao(c.getNome());
                aprovado.setBandeira(c.getBandeira());

                return aprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClientenotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }

    }

    public ProtocoloSolicitacaoCartao solicitacaoEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try {
            solicitacaoEmissaoCartaoPublisher.solicitacaoCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
