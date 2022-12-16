package com.curso.udemy.avaliadorcredito.msavaliadorcredito.application.ex;

public class DadosClientenotFoundException extends Exception{

    public DadosClientenotFoundException(){
        super("Dados do cliente não encontrados para o CPF informado.");
    }
}
