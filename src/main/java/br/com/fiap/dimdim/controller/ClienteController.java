package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.model.Cliente;
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

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clientes;
    private final ContaRepository contas;

    public ClienteController(ClienteRepository clientes, ContaRepository contas) {
        this.clientes = clientes;
        this.contas = contas;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clientes.findAll(Sort.by("nome")));
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return clientes.findById(id)
                .map(c -> {
                    model.addAttribute("cliente", c);
                    return "clientes/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Cliente " + id + " não encontrado.");
                    return "redirect:/clientes";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("cliente") Cliente cliente,
                         BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "clientes/form";
        }
        boolean novo = cliente.getId() == null;
        try {
            clientes.save(cliente);
        } catch (DataIntegrityViolationException e) {
            result.rejectValue("cpf", "duplicado", "Já existe um cliente com esse CPF.");
            return "clientes/form";
        }
        ra.addFlashAttribute("sucesso", novo ? "Cliente cadastrado." : "Cliente atualizado.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        if (contas.countByClienteId(id) > 0) {
            ra.addFlashAttribute("erro", "Esse cliente ainda tem contas. Exclua as contas dele primeiro.");
            return "redirect:/clientes";
        }
        clientes.deleteById(id);
        ra.addFlashAttribute("sucesso", "Cliente excluído.");
        return "redirect:/clientes";
    }
}
