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

@Service
public class ExportService {
    
    // ============== EXPORTAR CLIENTES ==============
    
    public byte[] exportarClientesPDF(List<ClienteDTO> clientes) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, baos);
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Listado de Clientes", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"ID", "Nombre", "NIF", "Email", "Teléfono", "Ciudad"});
        
        for (ClienteDTO cliente : clientes) {
            table.addCell(cliente.getId().toString());
            table.addCell(cliente.getNombre());
            table.addCell(cliente.getNif());
            table.addCell(cliente.getEmail() != null ? cliente.getEmail() : "-");
            table.addCell(cliente.getTelefono() != null ? cliente.getTelefono() : "-");
            table.addCell(cliente.getCiudad() != null ? cliente.getCiudad() : "-");
        }
        
        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }
    
    public String exportarClientesCSV(List<ClienteDTO> clientes) throws IOException {
        StringWriter sw = new StringWriter();
        ICSVWriter csvWriter = new CSVWriter(sw, 
                                             ';', 
                                             ICSVWriter.DEFAULT_QUOTE_CHARACTER,
                                             ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                             ICSVWriter.DEFAULT_LINE_END);
        
        String[] header = {"ID", "Nombre", "NIF", "Email", "Teléfono", "Dirección", "Ciudad", "Provincia", "CP"};
        csvWriter.writeNext(header);
        
        for (ClienteDTO cliente : clientes) {
            String[] data = {
                cliente.getId().toString(),
                cliente.getNombre(),
                cliente.getNif(),
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
        return sw.toString();
    }
    
    // ============== EXPORTAR PEDIDOS ==============
    
    public byte[] exportarPedidosPDF(List<PedidoDTO> pedidos) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, baos);
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Listado de Pedidos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"ID", "Cliente", "Fecha", "Estado", "Total Base", "Total Final"});
        
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
    
    public String exportarPedidosCSV(List<PedidoDTO> pedidos) throws IOException {
        StringWriter sw = new StringWriter();
        ICSVWriter csvWriter = new CSVWriter(sw, 
                                             ';', 
                                             ICSVWriter.DEFAULT_QUOTE_CHARACTER,
                                             ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                             ICSVWriter.DEFAULT_LINE_END);
        
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
    
    public byte[] exportarFacturasPDF(List<FacturaDTO> facturas) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, baos);
        document.open();
        
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Listado de Facturas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        
        addTableHeader(table, new String[]{"Nº Factura", "Cliente", "Fecha Emisión", "Vencimiento", "Estado", "Base", "Total"});
        
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
    
    private void addTableHeader(PdfPTable table, String[] headers) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
}
