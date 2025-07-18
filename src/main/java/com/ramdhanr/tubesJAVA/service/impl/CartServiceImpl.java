package com.ramdhanr.tubesJAVA.service.impl;

import com.ramdhanr.tubesJAVA.model.CartItem;
import com.ramdhanr.tubesJAVA.model.Product;
import com.ramdhanr.tubesJAVA.model.ShoppingCart;
import com.ramdhanr.tubesJAVA.model.User;
import com.ramdhanr.tubesJAVA.repository.CartItemRepository;
import com.ramdhanr.tubesJAVA.repository.ShoppingCartRepository;
import com.ramdhanr.tubesJAVA.service.CartService;
import com.ramdhanr.tubesJAVA.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.math.BigDecimal;
import java.util.Collections; 
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartServiceImpl(ShoppingCartRepository shoppingCartRepository,
                           CartItemRepository cartItemRepository,
                           ProductService productService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    @Override
    @Transactional 
    public ShoppingCart getCartForUser(User user) {
        return shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });
    }

    @Override
    @Transactional 
    public CartItem addProductToCart(User user, Integer productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity harus lebih dari 0.");
        }

        ShoppingCart cart = getCartForUser(user); 
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new RuntimeException("Produk dengan ID " + productId + " tidak ditemukan."));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stok produk '" + product.getName() + "' tidak mencukupi (tersisa: " + product.getStock() + ").");
        }

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByShoppingCartIdAndProductId(cart.getId(), productId);

        CartItem cartItem;
        if (existingCartItemOpt.isPresent()) {
            cartItem = existingCartItemOpt.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                 throw new RuntimeException("Stok produk '" + product.getName() + "' tidak mencukupi untuk jumlah total (tersisa: " + product.getStock() + ").");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setShoppingCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        return cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional 
    public CartItem updateCartItemQuantity(User user, Integer cartItemId, int newQuantity) {
        ShoppingCart cart = getCartForUser(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item keranjang dengan ID " + cartItemId + " tidak ditemukan."));

        if (!cartItem.getShoppingCart().getId().equals(cart.getId())) {
            throw new SecurityException("Anda tidak berhak mengubah item keranjang ini.");
        }
        
        Product product = cartItem.getProduct();
        // Cek stok hanya jika quantity baru > quantity lama (user menambah item)
        if (newQuantity > cartItem.getQuantity() && product.getStock() < newQuantity) {
             throw new RuntimeException("Stok produk '" + product.getName() + "' tidak mencukupi untuk jumlah " + newQuantity + " (tersisa: " + product.getStock() + ").");
        }

        if (newQuantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null; 
        } else {
            cartItem.setQuantity(newQuantity);
            return cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional // Ini adalah operasi delete (write)
    public void removeCartItem(User user, Integer cartItemId) {
        ShoppingCart cart = getCartForUser(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item keranjang dengan ID " + cartItemId + " tidak ditemukan."));

        if (!cartItem.getShoppingCart().getId().equals(cart.getId())) {
            throw new SecurityException("Anda tidak berhak menghapus item keranjang ini.");
        }
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional 
    public void clearCart(User user) {
        ShoppingCart cart = getCartForUser(user);
        List<CartItem> items = cartItemRepository.findByShoppingCartId(cart.getId());
        if (items != null && !items.isEmpty()) {
            cartItemRepository.deleteAll(items);
        }
    }

    @Override
    @Transactional(readOnly = true) 
                        
    public BigDecimal getTotalCartPrice(User user) {
        ShoppingCart cart = getCartForUser(user); // Bisa create cart
        if (cart == null || cart.getId() == null) return BigDecimal.ZERO;

        List<CartItem> items = cartItemRepository.findByShoppingCartId(cart.getId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getProduct() != null && item.getProduct().getPrice() != null) {
                BigDecimal itemPrice = item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()));
                totalPrice = totalPrice.add(itemPrice);
            }
        }
        return totalPrice;
    }

    @Override
    @Transactional 
    public List<CartItem> getCartItems(User user) {
        ShoppingCart cart = getCartForUser(user); 
        if (cart == null || cart.getId() == null) {
           
            return Collections.emptyList(); 
        }
        return cartItemRepository.findByShoppingCartId(cart.getId());
    }
}