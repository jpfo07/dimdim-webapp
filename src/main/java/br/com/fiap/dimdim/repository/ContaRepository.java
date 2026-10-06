package br.com.fiap.dimdim.repository;

import br.com.fiap.dimdim.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    long countByClienteId(Long clienteId);

    @Query("select sum(c.saldo) from Conta c")
    BigDecimal somarSaldos();
}
