package com.example.demo.controllers;

import com.example.demo.helper.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

    }

    @Test
    public void verify_get_items (){

        when(itemRepo.findAll()).thenReturn(initializeItemList());

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List <Item> items = response.getBody();

        assertEquals(initializeItemList(), items);

        assertEquals(2,items.size());
        assertEquals("Name item2", items.get(1).getName());

    }

    @Test
    public void verify_get_item_by_Id (){

        List<Item> initializedList = initializeItemList();

        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.ofNullable(initializedList.get(0)));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item retrievedItem = response.getBody();

        assertEquals("description item1",retrievedItem.getDescription());
        assertEquals("Name item1", retrievedItem.getName());
        assertEquals(new BigDecimal(1), retrievedItem.getPrice());

    }

    @Test
    public void verify_get_items_by_name (){
        List<Item> initializedList = initializeItemList();
        String nameOfExpectedItem = initializedList.get(0).getName();

        Item item3WithSameName = new Item();
        item3WithSameName.setName(nameOfExpectedItem);
        item3WithSameName.setDescription("description item3");
        item3WithSameName.setPrice(new BigDecimal(1.30));

        List <Item> expectedListItemsWithSameName = new ArrayList<>();
        expectedListItemsWithSameName.add(initializedList.get(0));
        expectedListItemsWithSameName.add(item3WithSameName);

        when(itemRepo.findByName(nameOfExpectedItem)).thenReturn(expectedListItemsWithSameName);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(nameOfExpectedItem);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List <Item> items = response.getBody();

        assertEquals(expectedListItemsWithSameName, items);

        assertEquals(2,items.size());
        assertEquals(nameOfExpectedItem, items.get(0).getName());
        assertEquals(nameOfExpectedItem, items.get(1).getName());

    }


    public List<Item> initializeItemList(){
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Name item1");
        item1.setDescription("description item1");
        item1.setPrice(new BigDecimal(1.00));
        items.add(item1);

        Item item2 = new Item();
        item2.setName("Name item2");
        item2.setId(2L);
        item2.setDescription("description item2");
        item2.setPrice(new BigDecimal(1.20));
        items.add(item2);

        return items;
    }



}
