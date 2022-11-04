package com.example.demo.controllers;

import com.example.demo.helper.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    @Autowired
    private CartControllerTest cartControllerTest;

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        cartControllerTest = new CartControllerTest();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submit_order_happy_path(){

        User userWithCart = cartControllerTest.initializeUserWithCart();
        when(userRepo.findByUsername(userWithCart.getUsername())).thenReturn(userWithCart);


        ResponseEntity<UserOrder> response = orderController.submit(userWithCart.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrderResponse = response.getBody();
        assertEquals(userWithCart.getCart().getItems(), userOrderResponse.getItems());
        assertEquals(userWithCart, userOrderResponse.getUser());
        assertEquals(userWithCart.getCart().getTotal(),userOrderResponse.getTotal() );

    }

    @Test
    public void submit_order_without_User_in_request(){

        ResponseEntity<UserOrder> response = orderController.submit("noUsername");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }

    @Test
    public void get_orders_for_user_happy_path(){

        User userWithCart = cartControllerTest.initializeUserWithCart();
        when(userRepo.findByUsername(userWithCart.getUsername())).thenReturn(userWithCart);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(userWithCart.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    public void get_orders_without_User_in_request(){

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("noUsername");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }





}
