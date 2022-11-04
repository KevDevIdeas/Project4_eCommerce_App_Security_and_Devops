package com.example.demo.controllers;

import com.example.demo.helper.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;


    private UserRepository userRepo = mock(UserRepository.class);


    private CartRepository cartRepo = mock(CartRepository.class);


    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path(){

        when(encoder.encode("testPassword")).thenReturn("testPwHashed");

        CreateUserRequest r = createUserRequestWithStandardInfo();

        final ResponseEntity<User> response = userController.createUser(r);

        // assertion of response
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        //assertion if output is the same as input
        User u = response.getBody();

        assertEquals(0, u.getId());
        assertEquals("testUser", u.getUsername());
        assertEquals("testPwHashed", u.getPassword());



    }

    @Test
    public void create_user_short_password(){

        when(encoder.encode("testPw")).thenReturn("testPwHashed");

        CreateUserRequest r = createUserRequestWithStandardInfo();

        r.setPassword("testPw");
        r.setConfirmPassword("testPw");

        final ResponseEntity<User> response = userController.createUser(r);

        // assertion if response returns 400 bad Request, because the password does not consist of <7 Chars
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());



    }

    @Test
    public void create_user_no_password_matching(){

        when(encoder.encode("testPw")).thenReturn("testPwHashed");

        CreateUserRequest r = createUserRequestWithStandardInfo();

        r.setPassword("testPassword");
        r.setConfirmPassword("DifferentTestPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        // assertion if response returns 400 bad Request, because the password does not consist of <7 Chars
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());



    }

    @Test
    public void verify_find_by_id(){
        when(encoder.encode("testPassword")).thenReturn("testPwHashed");


        CreateUserRequest r = createUserRequestWithStandardInfo();

        final ResponseEntity<User> responseUserCreated = userController.createUser(r);

        assertNotNull(responseUserCreated);
        assertEquals(200, responseUserCreated.getStatusCodeValue());

        User userCreated = responseUserCreated.getBody();
        Optional<User> optionalUser = Optional.of(userCreated);

        when(userRepo.findById(0L)).thenReturn(optionalUser);

        final ResponseEntity<User> responseFindById = userController.findById(userCreated.getId());

        assertNotNull(responseFindById);
        assertEquals(200, responseFindById.getStatusCodeValue());


        User userFoundById = responseFindById.getBody();

        assertEquals(userCreated.getUsername(), userFoundById.getUsername());
        assertEquals(userCreated.getId(), userFoundById.getId());
        assertEquals(userCreated.getPassword(), userFoundById.getPassword());


    }

    @Test
    public void verify_find_by_name_happy_path(){
        when(encoder.encode("testPassword")).thenReturn("testPwHashed");


        CreateUserRequest r = createUserRequestWithStandardInfo();

        final ResponseEntity<User> responseUserCreated = userController.createUser(r);

        assertNotNull(responseUserCreated);
        assertEquals(200, responseUserCreated.getStatusCodeValue());

        User userCreated = responseUserCreated.getBody();

        when(userRepo.findByUsername(userCreated.getUsername())).thenReturn(userCreated);

        final ResponseEntity<User> responseFindByName = userController.findByUserName(userCreated.getUsername());

        assertNotNull(responseFindByName);
        assertEquals(200, responseFindByName.getStatusCodeValue());


        User userFoundByName = responseFindByName.getBody();

        assertEquals(userCreated.getUsername(), userFoundByName.getUsername());
        assertEquals(userCreated.getId(), userFoundByName.getId());
        assertEquals(userCreated.getPassword(), userFoundByName.getPassword());


    }

    @Test
    public void verify_find_by_name_negative(){
        when(encoder.encode("testPassword")).thenReturn("testPwHashed");


        CreateUserRequest r = createUserRequestWithStandardInfo();

        final ResponseEntity<User> responseUserCreated = userController.createUser(r);

        assertNotNull(responseUserCreated);
        assertEquals(200, responseUserCreated.getStatusCodeValue());

        User userCreated = responseUserCreated.getBody();

        //without mocking that test will result in 404 not found
        //when(userRepo.findByUsername(userCreated.getUsername())).thenReturn(userCreated);

        final ResponseEntity<User> responseFindByName = userController.findByUserName(userCreated.getUsername());

        assertNotNull(responseFindByName);
        assertEquals(404, responseFindByName.getStatusCodeValue());



    }

    public CreateUserRequest createUserRequestWithStandardInfo(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        return createUserRequest;
    }



}
