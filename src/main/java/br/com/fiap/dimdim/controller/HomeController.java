package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.repository.ClienteRepository;
import br.com.fiap.dimdim.repository.ContaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ClienteRepository clientes;
    private final ContaRepository contas;

    public HomeController(ClienteRepository clientes, ContaRepository contas) {
        this.clientes = clientes;
        this.contas = contas;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalClientes", clientes.count());
        model.addAttribute("totalContas", contas.count());
        java.math.BigDecimal saldo = contas.somarSaldos();
        model.addAttribute("saldoTotal", saldo == null ? java.math.BigDecimal.ZERO : saldo);
        return "index";
    }
}
