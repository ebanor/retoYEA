package com.mikeldi.reto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Marca esta clase como Controller (no RestController) para retornar vistas HTML
@Controller
// Establece la ruta base /web para todas las páginas de la interfaz web
@RequestMapping("/web")
public class WebController {
    
    // Ruta raíz que redirige automáticamente a la página de login
    @GetMapping
    public String index() {
        return "redirect:/web/login";
    }
    
    // Página de inicio de sesión del sistema
    @GetMapping("/login")
    public String login() {
        // Resuelve a templates/auth/login.html
        return "auth/login";
    }
    
    // Panel principal después de autenticarse
    @GetMapping("/dashboard")
    public String dashboard() {
        // Resuelve a templates/dashboard.html
        return "dashboard";
    }
    
    // Página que muestra el listado de todos los clientes
    @GetMapping("/clientes")
    public String clientes() {
        // Resuelve a templates/clientes/lista.html
        return "clientes/lista";
    }
    
    // Formulario para crear un nuevo cliente
    @GetMapping("/clientes/crear")
    public String crearCliente() {
        return "clientes/crear";
    }
    
    // Formulario para editar un cliente existente
    @GetMapping("/clientes/editar")
    public String editarCliente() {
        return "clientes/editar";
    }
    
    // Página que muestra el catálogo de productos
    @GetMapping("/productos")
    public String productos() {
        return "productos/lista";
    }
    
    // Formulario para dar de alta un nuevo producto
    @GetMapping("/productos/crear")
    public String crearProducto() {
        return "productos/crear";
    }
    
    // Formulario para modificar un producto existente
    @GetMapping("/productos/editar")
    public String editarProducto() {
        return "productos/editar";
    }
    
    // Página que muestra el listado de pedidos
    @GetMapping("/pedidos")
    public String pedidos() {
        return "pedidos/lista";
    }

    // Formulario para crear un nuevo pedido con sus líneas
    @GetMapping("/pedidos/crear")
    public String crearPedido() {
        return "pedidos/crear";
    }

    // Vista detallada de un pedido específico
    @GetMapping("/pedidos/ver")
    public String verPedido() {
        return "pedidos/ver";
    }

    // Página que muestra el listado de facturas emitidas
    @GetMapping("/facturas")
    public String facturas() {
        return "facturas/lista";
    }

    // Formulario para emitir una nueva factura desde un pedido
    @GetMapping("/facturas/emitir")
    public String emitirFactura() {
        return "facturas/emitir";
    }

    // Vista detallada de una factura específica
    @GetMapping("/facturas/ver")
    public String verFactura() {
        return "facturas/ver";
    }

    // Página que muestra el historial de movimientos de stock
    @GetMapping("/stock")
    public String stock() {
        return "stock/movimientos";
    }

    // Formulario para registrar entrada, salida o ajuste de stock
    @GetMapping("/stock/registrar")
    public String registrarMovimiento() {
        return "stock/registrar";
    }

    // Página de reportes y estadísticas del sistema
    @GetMapping("/reportes")
    public String reportes() {
        return "reportes/index";
    }
    
    // Página de gestión y administración de usuarios
    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios/lista";
    }
}
