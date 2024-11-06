package com.example.lastproject.domain.item.repository;

import com.example.lastproject.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQueryRepository {

    @Query("SELECT i.productName FROM Item i")
    List<String> findAllByProductNames();

}
