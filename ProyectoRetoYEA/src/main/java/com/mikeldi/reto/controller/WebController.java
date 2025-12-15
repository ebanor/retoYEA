package com.mikeldi.reto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class WebController {
    
    @GetMapping
    public String index() {
        return "redirect:/web/login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/clientes")
    public String clientes() {
        return "clientes/lista";
    }
    
    @GetMapping("/clientes/crear")
    public String crearCliente() {
        return "clientes/crear";
    }
    
    @GetMapping("/clientes/editar")
    public String editarCliente() {
        return "clientes/editar";
    }
    
    @GetMapping("/productos")
    public String productos() {
        return "productos/lista";
    }
    
    @GetMapping("/productos/crear")
    public String crearProducto() {
        return "productos/crear";
    }
    
    @GetMapping("/productos/editar")
    public String editarProducto() {
        return "productos/editar";
    }
    
    @GetMapping("/pedidos")
    public String pedidos() {
        return "pedidos/lista";
    }

    @GetMapping("/pedidos/crear")
    public String crearPedido() {
        return "pedidos/crear";
    }

    @GetMapping("/pedidos/ver")
    public String verPedido() {
        return "pedidos/ver";
    }

    @GetMapping("/facturas")
    public String facturas() {
        return "facturas/lista";
    }

    @GetMapping("/facturas/emitir")
    public String emitirFactura() {
        return "facturas/emitir";
    }

    @GetMapping("/facturas/ver")
    public String verFactura() {
        return "facturas/ver";
    }

    @GetMapping("/stock")
    public String stock() {
        return "stock/movimientos";
    }

    @GetMapping("/stock/registrar")
    public String registrarMovimiento() {
        return "stock/registrar";
    }

    @GetMapping("/reportes")
    public String reportes() {
        return "reportes/index";
    }
    
    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios/lista";
    }
}
