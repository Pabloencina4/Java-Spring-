package com.application.rest.controller;

import com.application.rest.controller.dto.PurchaseDTO;
import com.application.rest.controller.dto.PurchaseItemDTO;
import com.application.rest.entities.Product;
import com.application.rest.entities.Purchase;
import com.application.rest.entities.PurchaseItems;
import com.application.rest.entities.UserEntity;
import com.application.rest.repository.UserRepository;
import com.application.rest.service.IProductService;
import com.application.rest.service.IPurchaseService;
import com.application.rest.service.impl.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private IProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IPurchaseService purchaseService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/make")
    public ResponseEntity<?> make(@RequestBody PurchaseDTO purchaseDTO) throws URISyntaxException {

        if (purchaseDTO.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setCompraDate(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<PurchaseItems> items = new ArrayList<>();

        for (PurchaseItemDTO itemDTO : purchaseDTO.getItems()) {
            Product product = productService.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getCant()));
            total = total.add(subtotal);

            PurchaseItems item = new PurchaseItems();
            item.setProduct(product);
            item.setCant(itemDTO.getCant());
            item.setPrice(product.getPrice());
            item.setPurchase(purchase);

            items.add(item);
        }

        purchase.setItems(items);
        purchase.setTotal(total);

        purchaseService.save(purchase);

        return ResponseEntity.created(new URI("/api/purchase/make")).build();
    }


    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Optional<Purchase> purchaseOptional = purchaseService.findById(id);

        if (purchaseOptional.isPresent()) {
            Purchase purchase = purchaseOptional.get();

            PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                    .id(purchase.getId())
                    .username(purchase.getUser().getUsername())
                    .compraDate(purchase.getCompraDate())
                    .total(purchase.getTotal())
                    .items(
                            purchase.getItems().stream()
                                    .map(item -> PurchaseItemDTO.builder()
                                            .productId(item.getProduct().getId())
                                            .productName(item.getProduct().getName())
                                            .price(item.getPrice())
                                            .cant(item.getCant())
                                            .build()
                                    )
                                    .toList()
                    )
                    .build();

            return ResponseEntity.ok(purchaseDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/my-purchases")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> findMyPurchases() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<PurchaseDTO> purchases = purchaseService.findAll().stream()
                .filter(purchase -> purchase.getUser().getId().equals(user.getId()))
                .map(purchase -> PurchaseDTO.builder()
                        .id(purchase.getId())
                        .username(purchase.getUser().getUsername())
                        .compraDate(purchase.getCompraDate())
                        .total(purchase.getTotal())
                        .items(
                                purchase.getItems().stream()
                                        .map(item -> PurchaseItemDTO.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .price(item.getPrice())
                                                .cant(item.getCant())
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
                )
                .toList();

        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findBy(){
        List<PurchaseDTO> purchaseList = purchaseService.findAll()
                .stream()
                .map(purchase -> PurchaseDTO.builder()
                        .id(purchase.getId())
                        .username(purchase.getUser().getUsername())
                        .compraDate(purchase.getCompraDate())
                        .total(purchase.getTotal())
                        .items(
                                purchase.getItems().stream()
                                        .map(item -> PurchaseItemDTO.builder()
                                                .productId(item.getProduct().getId())
                                                .productName(item.getProduct().getName())
                                                .price(item.getPrice())
                                                .cant(item.getCant())
                                                .build()
                                        )
                                        .toList()
                        ).build()
                )
                .toList();
        return ResponseEntity.ok(purchaseList);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteById(@PathVariable Long id){

        if(id!=null){
            purchaseService.deleteById(id);
            return ResponseEntity.ok("Compra eliminada");
        }

        return ResponseEntity.badRequest().build();
    }
}

