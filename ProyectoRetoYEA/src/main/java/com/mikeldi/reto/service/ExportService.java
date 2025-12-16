package com.mikeldi.reto.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mikeldi.reto.dto.ClienteDTO;
import com.mikeldi.reto.dto.FacturaDTO;
import com.mikeldi.reto.dto.PedidoDTO;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

// Servicio especializado en exportar datos a formatos PDF y CSV
@Service
public class ExportService {
    
    // ============== EXPORTAR CLIENTES ==============
    
    // Genera un PDF con la lista de clientes en formato tabla
    public byte[] exportarClientesPDF(List<ClienteDTO> clientes) throws DocumentException {
        // Crea documento en formato A4 horizontal para tablas anchas
        Document document = new Document(PageSize.A4.rotate());
        // ByteArrayOutputStream permite retornar el PDF como array de bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Asocia el writer con el documento y el stream de salida
        PdfWriter.getInstance(document, baos);
        document.open();
        
        // Añade título del documento centrado con fuente grande y negrita
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Listado de Clientes", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);  // Espacio después del título
        document.add(title);
        
        // Crea tabla con 6 columnas para los datos de clientes
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);  // Ocupa el 100% del ancho de página
        
        // Añade encabezados de tabla con estilo
        addTableHeader(table, new String[]{"ID", "Nombre", "NIF", "Email", "Teléfono", "Ciudad"});
        
        // Itera sobre cada cliente y añade sus datos como fila
        for (ClienteDTO cliente : clientes) {
            table.addCell(cliente.getId().toString());
            table.addCell(cliente.getNombre());
            table.addCell(cliente.getNif());
            // Usa "-" para valores nulos en lugar de mostrar "null"
            table.addCell(cliente.getEmail() != null ? cliente.getEmail() : "-");
            table.addCell(cliente.getTelefono() != null ? cliente.getTelefono() : "-");
            table.addCell(cliente.getCiudad() != null ? cliente.getCiudad() : "-");
        }
        
        document.add(table);
        document.close();
        
        // Retorna el PDF como array de bytes para descarga
        return baos.toByteArray();
    }
    
    // Genera un CSV con la lista de clientes usando punto y coma como separador
    public String exportarClientesCSV(List<ClienteDTO> clientes) throws IOException {
        // StringWriter acumula el contenido CSV en memoria
        StringWriter sw = new StringWriter();
        // Configura CSVWriter con punto y coma (estándar en Excel europeo)
        ICSVWriter csvWriter = new CSVWriter(sw, 
                                             ';',  // Separador de campos
                                             ICSVWriter.DEFAULT_QUOTE_CHARACTER,
                                             ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                             ICSVWriter.DEFAULT_LINE_END);
        
        // Escribe la fila de encabezados
        String[] header = {"ID", "Nombre", "NIF", "Email", "Teléfono", "Dirección", "Ciudad", "Provincia", "CP"};
        csvWriter.writeNext(header);
        
        // Escribe cada cliente como fila de datos
        for (ClienteDTO cliente : clientes) {
            String[] data = {
                cliente.getId().toString(),
                cliente.getNombre(),
                cliente.getNif(),
                // Convierte nulls a strings vacíos para CSV limpio
                cliente.getEmail() != null ? cliente.getEmail() : "",
                cliente.getTelefono() != null ? cliente.getTelefono() : "",
                cliente.getDireccion() != null ? cliente.getDireccion() : "",
                cliente.getCiudad() != null ? cliente.getCiudad() : "",
                cliente.getProvincia() != null ? cliente.getProvincia() : "",
                cliente.getCodigoPostal() != null ? cliente.getCodigoPostal() : ""
            };
            csvWriter.writeNext(data);
        }
        
        csvWriter.close();
        // Retorna el CSV como string completo
        return sw.toString();
    }
    
    // ============== EXPORTAR PEDIDOS ==============
    
    // Genera un PDF con la lista de pedidos en formato tabla
    public byte[] exportarPedidosPDF(List<PedidoDTO> pedidos) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, baos);
        document.open();
        
        // Título del reporte de pedidos
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Listado de Pedidos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Tabla con 6 columnas para información resumida de pedidos
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"ID", "Cliente", "Fecha", "Estado", "Total Base", "Total Final"});
        
        // Añade cada pedido con sus datos principales y totales con símbolo €
        for (PedidoDTO pedido : pedidos) {
            table.addCell(pedido.getId().toString());
            table.addCell(pedido.getClienteNombre());
            table.addCell(pedido.getFechaPedido().toString());
            table.addCell(pedido.getEstado().toString());
            table.addCell(pedido.getTotalBase().toString() + " €");
            table.addCell(pedido.getTotalFinal().toString() + " €");
        }
        
        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }
    
    // Genera un CSV con la lista de pedidos incluyendo más detalles
    public String exportarPedidosCSV(List<PedidoDTO> pedidos) throws IOException {
        StringWriter sw = new StringWriter();
        ICSVWriter csvWriter = new CSVWriter(sw, 
                                             ';', 
                                             ICSVWriter.DEFAULT_QUOTE_CHARACTER,
                                             ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                             ICSVWriter.DEFAULT_LINE_END);
        
        // CSV incluye más campos que el PDF para análisis detallado
        String[] header = {"ID", "Cliente", "Usuario", "Fecha", "Estado", "Total Base", "IVA", "Total Final", "Observaciones"};
        csvWriter.writeNext(header);
        
        for (PedidoDTO pedido : pedidos) {
            String[] data = {
                pedido.getId().toString(),
                pedido.getClienteNombre(),
                pedido.getUsuarioNombre(),
                pedido.getFechaPedido().toString(),
                pedido.getEstado().toString(),
                pedido.getTotalBase().toString(),
                pedido.getTotalIva().toString(),
                pedido.getTotalFinal().toString(),
                pedido.getObservaciones() != null ? pedido.getObservaciones() : ""
            };
            csvWriter.writeNext(data);
        }
        
        csvWriter.close();
        return sw.toString();
    }
    
    // ============== EXPORTAR FACTURAS ==============
    
    // Genera un PDF con la lista de facturas en formato tabla
    public byte[] exportarFacturasPDF(List<FacturaDTO> facturas) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, baos);
        document.open();
        
        // Título del reporte de facturas
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Listado de Facturas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Tabla con 7 columnas incluyendo número de factura y fechas
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"Nº Factura", "Cliente", "Fecha Emisión", "Vencimiento", "Estado", "Base", "Total"});
        
        // Añade cada factura con información fiscal completa
        for (FacturaDTO factura : facturas) {
            table.addCell(factura.getNumeroFactura());
            table.addCell(factura.getClienteNombre());
            table.addCell(factura.getFechaEmision().toString());
            table.addCell(factura.getFechaVencimiento().toString());
            table.addCell(factura.getEstado().toString());
            table.addCell(factura.getTotalBase().toString() + " €");
            table.addCell(factura.getTotalFinal().toString() + " €");
        }
        
        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }
    
    // Genera un CSV con la lista de facturas para análisis en Excel
    public String exportarFacturasCSV(List<FacturaDTO> facturas) throws IOException {
        StringWriter sw = new StringWriter();
        ICSVWriter csvWriter = new CSVWriter(sw, 
                                             ';', 
                                             ICSVWriter.DEFAULT_QUOTE_CHARACTER,
                                             ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                             ICSVWriter.DEFAULT_LINE_END);
        
        String[] header = {"Nº Factura", "Cliente", "Fecha Emisión", "Vencimiento", "Estado", "Total Base", "IVA", "Total Final", "Observaciones"};
        csvWriter.writeNext(header);
        
        for (FacturaDTO factura : facturas) {
            String[] data = {
                factura.getNumeroFactura(),
                factura.getClienteNombre(),
                factura.getFechaEmision().toString(),
                factura.getFechaVencimiento().toString(),
                factura.getEstado().toString(),
                factura.getTotalBase().toString(),
                factura.getTotalIva().toString(),
                factura.getTotalFinal().toString(),
                factura.getObservaciones() != null ? factura.getObservaciones() : ""
            };
            csvWriter.writeNext(data);
        }
        
        csvWriter.close();
        return sw.toString();
    }
    
    // ============== MÉTODO AUXILIAR ==============
    
    // Añade fila de encabezado con estilo consistente a tablas PDF
    private void addTableHeader(PdfPTable table, String[] headers) {
        // Fuente blanca negrita para encabezados
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        for (String header : headers) {
            // Crea celda con fondo gris oscuro y texto centrado
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);  // Padding para mejor legibilidad
            table.addCell(cell);
        }
    }
}
