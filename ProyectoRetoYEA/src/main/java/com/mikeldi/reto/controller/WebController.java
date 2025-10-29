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
}
