package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.model.Cliente;
import br.com.fiap.dimdim.model.Conta;
import br.com.fiap.dimdim.model.TipoConta;
import br.com.fiap.dimdim.repository.ClienteRepository;
import br.com.fiap.dimdim.repository.ContaRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/contas")
public class ContaController {

    private final ContaRepository contas;
    private final ClienteRepository clientes;

    public ContaController(ContaRepository contas, ClienteRepository clientes) {
        this.contas = contas;
        this.clientes = clientes;
    }

    @ModelAttribute("tipos")
    public TipoConta[] tipos() {
        return TipoConta.values();
    }

    @ModelAttribute("listaClientes")
    public Iterable<Cliente> listaClientes() {
        return clientes.findAll(Sort.by("nome"));
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contas", contas.findAll(Sort.by("id")));
        return "contas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model, RedirectAttributes ra) {
        if (clientes.count() == 0) {
            ra.addFlashAttribute("erro", "Cadastre um cliente antes de abrir uma conta.");
            return "redirect:/clientes/novo";
        }
        model.addAttribute("conta", new Conta());
        return "contas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return contas.findById(id)
                .map(c -> {
                    model.addAttribute("conta", c);
                    return "contas/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Conta " + id + " não encontrada.");
                    return "redirect:/contas";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("conta") Conta conta,
                         BindingResult result,
                         @RequestParam(value = "clienteId", required = false) Long clienteId,
                         Model model, RedirectAttributes ra) {
        Optional<Cliente> cliente = clienteId == null ? Optional.empty() : clientes.findById(clienteId);
        if (cliente.isEmpty()) {
            model.addAttribute("erroCliente", "Escolha o titular da conta.");
        } else {
            conta.setCliente(cliente.get());
        }
        if (result.hasErrors() || cliente.isEmpty()) {
            return "contas/form";
        }
        boolean nova = conta.getId() == null;
        try {
            contas.save(conta);
        } catch (DataIntegrityViolationException e) {
            result.rejectValue("numero", "duplicado", "Já existe uma conta com esse número.");
            return "contas/form";
        }
        ra.addFlashAttribute("sucesso", nova ? "Conta aberta." : "Conta atualizada.");
        return "redirect:/contas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        contas.deleteById(id);
        ra.addFlashAttribute("sucesso", "Conta encerrada.");
        return "redirect:/contas";
    }
}
