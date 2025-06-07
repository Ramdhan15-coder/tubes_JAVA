package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.dto.CheckoutFormDto;
import com.ramdhanr.tubesJAVA.model.*;
import com.ramdhanr.tubesJAVA.repository.OrderItemRepository;
import com.ramdhanr.tubesJAVA.repository.OrderRepository;
import com.ramdhanr.tubesJAVA.service.CartService;
import com.ramdhanr.tubesJAVA.service.OrderService;
import com.ramdhanr.tubesJAVA.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartService cartService,
                            ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    @Override
    @Transactional
    public Order placeOrder(User user, List<CartItem> cartItems, CheckoutFormDto checkoutFormDto) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Keranjang belanja kosong, tidak bisa membuat pesanan.");
        }

        // 1. Buat dan siapkan Order utama
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(new Timestamp(System.currentTimeMillis()));
        newOrder.setStatus("PENDING_PAYMENT"); // Status awal

        // Gabungkan detail alamat pengiriman
        String fullAddress = "Nama Penerima: " + checkoutFormDto.fullName() + "\n" +
                             "Nomor Telepon: " + checkoutFormDto.phoneNumber() + "\n" +
                             "Alamat Lengkap: " + checkoutFormDto.address();
        newOrder.setShippingAddress(fullAddress);
        newOrder.setNotes(checkoutFormDto.notes());

        // Hitung ulang total amount dari item keranjang untuk keamanan
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct() == null || cartItem.getProduct().getPrice() == null) {
                throw new RuntimeException("Detail produk tidak lengkap di keranjang.");
            }
            Product productInCart = cartItem.getProduct();
            if (productInCart.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Stok untuk produk '" + productInCart.getName() + "' tidak mencukupi.");
            }
            totalAmount = totalAmount.add(
                productInCart.getPrice().multiply(new BigDecimal(cartItem.getQuantity()))
            );
        }
        newOrder.setTotalAmount(totalAmount);

        // Simpan Order utama dulu untuk dapat ID
        Order savedOrder = orderRepository.save(newOrder);

        // 2. Buat dan simpan OrderItems yang terkait dengan Order utama
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice()); // Simpan harga saat itu
            orderItemRepository.save(orderItem);

            // 3. Kurangi stok produk
            productService.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }

        // 4. Kosongkan keranjang belanja user setelah order berhasil dibuat
        cartService.clearCart(user);

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderByIdForUser(Integer orderId, User user) {
        // Menggunakan metode repository yang sudah JOIN FETCH
        return orderRepository.findByIdAndUserWithItemsAndProducts(orderId, user);
    }
    
    @Override
    @Transactional
    public Order savePaymentProof(Integer orderId, String paymentProofUrl, User currentUser) {
        Order orderToUpdate = findOrderByIdForUser(orderId, currentUser)
                .orElseThrow(() -> new RuntimeException("Order dengan ID " + orderId + " tidak ditemukan atau bukan milik Anda."));

        if (!"PENDING_PAYMENT".equalsIgnoreCase(orderToUpdate.getStatus())) {
            throw new IllegalStateException("Bukti pembayaran hanya bisa diupload jika status pesanan adalah PENDING_PAYMENT.");
        }

        orderToUpdate.setPaymentProofUrl(paymentProofUrl);
        orderToUpdate.setStatus("WAITING_CONFIRMATION"); // Update status
        return orderRepository.save(orderToUpdate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrdersAdmin() {
        // Menggunakan metode repository baru yang sudah JOIN FETCH dengan User
        return orderRepository.findAllWithUserOrderByOrderDateAsc();
    }

    @Override
@Transactional(readOnly = true)
public Optional<Order> findOrderByIdAdmin(Integer orderId) {
    return orderRepository.findByIdWithItemsAndProductsAdmin(orderId);
}

// IMPLEMENTASI METODE BARU untuk updateOrderStatus
@Override
@Transactional
public Order updateOrderStatus(Integer orderId, String newStatus) {
    Order orderToUpdate = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Error: Pesanan dengan ID " + orderId + " tidak ditemukan."));

    // (Opsional) Tambahkan validasi untuk alur status, misal tidak bisa dari SHIPPED kembali ke PROCESSING, dll.
    // Untuk sekarang, kita biarkan admin bisa mengubah ke status apapun.

    orderToUpdate.setStatus(newStatus);
    return orderRepository.save(orderToUpdate);
}

    //IMPLEMENTASI METODE BARU UNTUK DELETE ORDER
    @Override
    @Transactional
    public void deleteOrderByIdAdmin(Integer orderId) {
        // 1. Cek apakah pesanan yang akan dihapus ada
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Error: Pesanan dengan ID " + orderId + " tidak ditemukan.");
        }
        
        // 2. Hapus pesanan
        // Karena di DDL kita ada ON DELETE CASCADE pada foreign key 'order_id' di tabel
        // 'order_items' dan 'payments', maka saat order ini dihapus, semua item dan
        // pembayaran terkait juga akan otomatis terhapus oleh database.
        orderRepository.deleteById(orderId);
    }
}