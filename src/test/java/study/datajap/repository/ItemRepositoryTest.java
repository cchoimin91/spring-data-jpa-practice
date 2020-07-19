package study.datajap.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajap.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;



    @Test
    public void save(){
        System.out.println("#####################");
        Item item = new Item("a");
        itemRepository.save(item);

        System.out.println(">>>>> item = " + item.getId());
    }



}