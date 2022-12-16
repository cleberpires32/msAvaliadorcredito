package com.curso.udemy.avaliadorcredito.msavaliadorcredito.domain;

import lombok.Data;
import org.bouncycastle.crypto.ec.ECPairFactorTransform;

import java.math.BigDecimal;

@Data
public class DadosSolicitacaoEmissaoCartao {

    private Long idCartao;
    private String cpf;
    private String endereco;
    private BigDecimal limiteLiberado;
}
