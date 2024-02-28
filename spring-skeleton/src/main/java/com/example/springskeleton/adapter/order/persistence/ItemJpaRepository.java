package com.example.springskeleton.adapter.order.persistence;

import com.example.springskeleton.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
}
