package com.example.demo.controllers;

import java.util.List;

import com.example.demo.util.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static  final Logger log = LogManager.getLogger(OrderController.class);
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		try{
			User user = userRepository.findByUsername(username);

		if(user == null) {
			log.error("Request: submitOrder, message: no user found for order");
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("Request: submitOrder, message: Items with total value of {} successfully ordered for user {}. Order details: {}", order.getTotal(),user.getUsername(), Mapper.mapToJsonString(order));
		return ResponseEntity.ok(order);
		} catch (Exception e) {
			log.error("Request: submitOrder, message: Failed to submitOrder");
			return new ResponseEntity("Server failure during processing", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Request: getOrdersForUser, message: no user found");
			return ResponseEntity.notFound().build();
		}
		log.info("Request: getOrdersForUser, message: order successfully retrieved for user {}", user.getUsername());
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
