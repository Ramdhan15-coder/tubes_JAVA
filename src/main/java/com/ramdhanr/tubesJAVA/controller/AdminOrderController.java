package com.ramdhanr.tubesJAVA.controller;

import com.ramdhanr.tubesJAVA.model.Order;
import com.ramdhanr.tubesJAVA.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;    // <-- IMPORT INI
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // <-- IMPORT INI
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Metode listAllOrders()
    @GetMapping
    public String listAllOrders(@RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "status", required = false) String status,
                                Model model) {

        List<Order> orderList = orderService.searchOrdersAdmin(keyword, status);
        model.addAttribute("orders", orderList);

        // Kirim kembali parameter pencarian ke view untuk diisi di form
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentStatus", status); 

        // Siapkan daftar status untuk dropdown filter
        List<String> statusList = List.of(
            "PENDING_PAYMENT", "WAITING_CONFIRMATION", "PAID", "PROCESSING",
            "SHIPPED", "DELIVERED", "CANCELED"
        );
        model.addAttribute("statusList", statusList);
        model.addAttribute("title", "Kelola Semua Pesanan");

        return "admin/orders/list-orders";
    }

    // Metode viewOrder
    @GetMapping("/view/{orderId}")
    public String viewOrder(@PathVariable("orderId") Integer orderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Order> orderOptional = orderService.findOrderByIdAdmin(orderId);
        if (orderOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pesanan dengan ID " + orderId + " tidak ditemukan.");
            return "redirect:/admin/orders";
        }
        Order order = orderOptional.get();
        model.addAttribute("order", order);
        model.addAttribute("title", "Detail Pesanan #" + order.getId());
        List<String> statusList = List.of(
            "PENDING_PAYMENT", "WAITING_CONFIRMATION", "PAID", "PROCESSING",
            "SHIPPED", "DELIVERED", "CANCELED"
        );
        model.addAttribute("statusList", statusList);
        return "admin/orders/view-order";
    }

    // METODE BARU UNTUK MEMPROSES UPDATE STATUS PESANAN
    @PostMapping("/update-status/{orderId}")
    public String updateOrderStatus(@PathVariable("orderId") Integer orderId,
                                    @RequestParam("status") String newStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Status untuk Pesanan #" + orderId + " berhasil diupdate menjadi '" + newStatus.replace("_", " ") + "'.");
        } catch (RuntimeException e) {
            
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengupdate status: " + e.getMessage());
        }
        // Redirect kembali ke halaman detail order yang sama
        return "redirect:/admin/orders/view/" + orderId;
    }


    // METODE BARU UNTUK MEMPROSES PENGHAPUSAN PESANAN
    @PostMapping("/delete/{orderId}") 
    public String deleteOrder(@PathVariable("orderId") Integer orderId, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrderByIdAdmin(orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Pesanan dengan ID #" + orderId + " berhasil dihapus secara permanen.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus pesanan: " + e.getMessage());
            // Jika gagal, kembali ke halaman detail order itu lagi
            return "redirect:/admin/orders/view/" + orderId;
        }
        
        return "redirect:/admin/orders";
    }

    
}