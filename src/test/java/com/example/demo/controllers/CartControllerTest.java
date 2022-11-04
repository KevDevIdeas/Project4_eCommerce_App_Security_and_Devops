package com.example.demo.controllers;

import com.example.demo.helper.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    @Autowired
    ItemControllerTest itemControllerTest;


    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void add_to_cart(){

        User user = initializeUserWithCart();
        // to the initial 2 items the two items from the ModifyRequest object will be added within cartController.addTocart
        Cart expectedCart = user.getCart();

        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.ofNullable(user.getCart().getItems().get(0)));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(cartRepo.save(user.getCart())).thenReturn(user.getCart());





        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername(user.getUsername());
        r.setItemId(1);
        r.setQuantity(2);


        ResponseEntity<Cart> response = cartController.addTocart(r);

        // assertion of response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        //assertion if output is the same as input
        Cart c = response.getBody();

        assertEquals(1L,(long) c.getId());
        assertEquals(expectedCart.getTotal(),c.getTotal());
    }

    @Test
    public void add_to_cart_without_User_in_request(){

        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(1);
        r.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }

    @Test
    public void add_to_cart_without_ItemId_in_request(){

        User user = initializeUserWithCart();

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername(user.getUsername());
        r.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }

    @Test
    public void remove_from_cart(){

        User user = initializeUserWithCart();
        // to the initial 2 items the two items from the ModifyRequest object will be added within cartController.addTocart
        Cart expectedCart = user.getCart();

        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.ofNullable(user.getCart().getItems().get(0)));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(cartRepo.save(user.getCart())).thenReturn(user.getCart());


        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername(user.getUsername());
        r.setItemId(1);
        r.setQuantity(1);


        ResponseEntity<Cart> response = cartController.removeFromcart(r);

        // assertion of response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        //assertion if output is the same as input
        Cart c = response.getBody();

        assertEquals(1L,(long) c.getId());
        assertEquals(expectedCart.getTotal(),c.getTotal());

        //Checking if now the item with Id 1 has been removed
        assertEquals(java.util.Optional.ofNullable(2L), java.util.Optional.ofNullable(c.getItems().get(0).getId()));

    }

    @Test
    public void remove_from_cart_without_User_in_request(){

        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(1);
        r.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }

    @Test
    public void remove_from_cart_without_ItemId_in_request(){

        User user = initializeUserWithCart();

        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername(user.getUsername());
        r.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }



    public User initializeUserWithCart(){
        itemControllerTest = new ItemControllerTest();
        User user = new User();
        user.setUsername("testUser");
        user.setId(1);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(itemControllerTest.initializeItemList());
        cart.setTotal(new BigDecimal(3));
        user.setCart(cart);
        cart.setUser(user);

        return user;
    }

}
