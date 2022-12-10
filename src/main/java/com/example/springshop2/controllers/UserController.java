package com.example.springshop2.controllers;

import com.example.springshop2.models.Cart;
import com.example.springshop2.models.Order;
import com.example.springshop2.models.Person;
import com.example.springshop2.models.Product;
import com.example.springshop2.repositories.CartRepository;
import com.example.springshop2.repositories.OrderRepository;
import com.example.springshop2.services.PersonService;
import com.example.springshop2.services.ProductService;
import com.example.springshop2.token.JWTTokenHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final JWTTokenHelper jWTTokenHelper;
    private final PersonService personService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public UserController(JWTTokenHelper jWTTokenHelper, PersonService personService, ProductService productService, OrderRepository orderRepository, CartRepository cartRepository) {
        this.jWTTokenHelper = jWTTokenHelper;
        this.personService = personService;
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping("/product/add_cart/{id}")
    public ResponseEntity<?> addToCart(HttpServletRequest request, @PathVariable("id") int idProduct) {
        String authToken = jWTTokenHelper.getToken(request);
        String login = jWTTokenHelper.getUsernameFromToken(authToken);
        Person person = personService.findByLogin(login);
        System.out.println("person:" + person.getId());
        Product product = productService.getProductId(idProduct);
        System.out.println("prod: " + product.getId() + " " + product.getTitle());
        Cart cart = new Cart(person, product);
        cartRepository.save(cart);
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/product/cart")
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        String authToken = jWTTokenHelper.getToken(request);
        String login = jWTTokenHelper.getUsernameFromToken(authToken);
        Person person = personService.findByLogin(login);

        List<Cart> productCart =  cartRepository.findByPersonId(person.getId());

        return ResponseEntity.ok(productCart);
    }

    @GetMapping("/product/delete_cart/{id}")
    public ResponseEntity<?> deleteToCart(HttpServletRequest request, @PathVariable("id") int idCart) {
        cartRepository.deleteById(idCart);
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/product/make_order")
    public ResponseEntity<?> makeOrder(HttpServletRequest request) {
        String authToken = jWTTokenHelper.getToken(request);
        String login = jWTTokenHelper.getUsernameFromToken(authToken);
        Person person = personService.findByLogin(login);

        List<Cart> cartList =  cartRepository.findByPersonId(person.getId());

        List<Product> productsList = new ArrayList<>();
        // Получаем продукты из корзины по id
        for (Cart cart: cartList) {
            productsList.add(productService.getProductId(cart.getProduct().getId()));
        }

        float price = 0;
        for (Product product: productsList){
            price += product.getPrice();
        }
        System.out.println(price);

        String uuid = UUID.randomUUID().toString();
        for (Product product: productsList){
            Order newOrder = new Order(product, person, price, uuid, "В обработке");
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }

        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/product/orders")
    public ResponseEntity<?> getOrders(HttpServletRequest request) {
        String authToken = jWTTokenHelper.getToken(request);
        String login = jWTTokenHelper.getUsernameFromToken(authToken);
        Person person = personService.findByLogin(login);

        List<Order> orderList = orderRepository.findByPerson(person);

        return ResponseEntity.ok(orderList);
    }
}
