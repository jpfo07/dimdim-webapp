package br.com.fiap.dimdim.controller;

import br.com.fiap.dimdim.model.Cliente;
import br.com.fiap.dimdim.model.Conta;
import br.com.fiap.dimdim.repository.ClienteRepository;
import br.com.fiap.dimdim.repository.ContaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST (JSON) com o mesmo CRUD do front-end.
 * Exemplos de JSON em docs/api-json.md
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final ClienteRepository clientes;
    private final ContaRepository contas;

    public ApiController(ClienteRepository clientes, ContaRepository contas) {
        this.clientes = clientes;
        this.contas = contas;
    }

    // ---------- CLIENTES ----------
    @GetMapping("/clientes")
    public List<Cliente> listarClientes() {
        return clientes.findAll();
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable Long id) {
        return ResponseEntity.of(clientes.findById(id));
    }

    @PostMapping("/clientes")
    public ResponseEntity<Cliente> criarCliente(@Valid @RequestBody Cliente cliente) {
        cliente.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientes.save(cliente));
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente dados) {
        return clientes.findById(id).map(c -> {
            c.setNome(dados.getNome());
            c.setCpf(dados.getCpf());
            c.setEmail(dados.getEmail());
            c.setTelefone(dados.getTelefone());
            return ResponseEntity.ok(clientes.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Object> excluirCliente(@PathVariable Long id) {
        if (!clientes.existsById(id)) return ResponseEntity.notFound().build();
        if (contas.countByClienteId(id) > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Cliente possui contas. Exclua as contas primeiro."));
        }
        clientes.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- CONTAS ----------
    @GetMapping("/contas")
    public List<Conta> listarContas() {
        return contas.findAll();
    }

    @GetMapping("/contas/{id}")
    public ResponseEntity<Conta> buscarConta(@PathVariable Long id) {
        return ResponseEntity.of(contas.findById(id));
    }

    @PostMapping("/contas")
    public ResponseEntity<Object> criarConta(@Valid @RequestBody Conta conta) {
        Long clienteId = conta.getCliente() == null ? null : conta.getCliente().getId();
        if (clienteId == null || !clientes.existsById(clienteId)) {
            return ResponseEntity.badRequest().body(Map.of("erro", "cliente.id inválido"));
        }
        conta.setId(null);
        conta.setCliente(clientes.findById(clienteId).get());
        return ResponseEntity.status(HttpStatus.CREATED).body(contas.save(conta));
    }

    @PutMapping("/contas/{id}")
    public ResponseEntity<Object> atualizarConta(@PathVariable Long id, @Valid @RequestBody Conta dados) {
        return contas.findById(id).<ResponseEntity<Object>>map(c -> {
            c.setNumero(dados.getNumero());
            c.setAgencia(dados.getAgencia());
            c.setTipo(dados.getTipo());
            c.setSaldo(dados.getSaldo());
            if (dados.getCliente() != null && dados.getCliente().getId() != null) {
                var cli = clientes.findById(dados.getCliente().getId());
                if (cli.isEmpty()) return ResponseEntity.badRequest().body(Map.of("erro", "cliente.id inválido"));
                c.setCliente(cli.get());
            }
            return ResponseEntity.ok(contas.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/contas/{id}")
    public ResponseEntity<Void> excluirConta(@PathVariable Long id) {
        if (!contas.existsById(id)) return ResponseEntity.notFound().build();
        contas.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> erros.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(erros);
    }
}
