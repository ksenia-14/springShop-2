package com.example.springshop2.controllers;

import com.example.springshop2.models.Product;
import com.example.springshop2.repositories.ProductRepository;
import com.example.springshop2.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    /* Получение всех товаров */
    @GetMapping("/all_products")
    public ResponseEntity<?> getAllProducts() {
        List<Product> productList =  productService.getAllProduct();
        return ResponseEntity.ok(productList);
    }

    /* Получение товара по id */
    @GetMapping("/get_product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") int id) {
        Product product =  productService.getProductId(id);
        return ResponseEntity.ok(product);
    }
}
