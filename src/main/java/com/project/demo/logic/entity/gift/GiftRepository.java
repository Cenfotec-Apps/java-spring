package com.project.demo.logic.entity.gift;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, Long> {
    Page<Gift> findByGiftListId(Long giftListId, Pageable pageable);
}