package com.project.demo.rest.gift;

import com.project.demo.logic.entity.gift.Gift;
import com.project.demo.logic.entity.gift.GiftRepository;
import com.project.demo.logic.entity.giftList.GiftList;
import com.project.demo.logic.entity.giftList.GiftListRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/gifts")
public class GiftRestController {
    
    @Autowired
    private GiftRepository giftRepository;
    
    @Autowired
    private GiftListRepository giftListRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Gift> giftsPage = giftRepository.findAll(pageable);
        
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(giftsPage.getTotalPages());
        meta.setTotalElements(giftsPage.getTotalElements());
        meta.setPageNumber(giftsPage.getNumber() + 1);
        meta.setPageSize(giftsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Gifts retrieved successfully",
                giftsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/gift-list/{giftListId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getGiftsByGiftListId(
            @PathVariable Long giftListId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Gift> giftsPage = giftRepository.findByGiftListId(giftListId, pageable);
        
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(giftsPage.getTotalPages());
        meta.setTotalElements(giftsPage.getTotalElements());
        meta.setPageNumber(giftsPage.getNumber() + 1);
        meta.setPageSize(giftsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Gifts retrieved successfully",
                giftsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getGiftById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Gift> foundGift = giftRepository.findById(id);
        if(foundGift.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Gift retrieved successfully",
                    foundGift.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Gift " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addGift(@RequestBody Gift gift, HttpServletRequest request) {
        if (gift.getGiftList() != null && gift.getGiftList().getId() != null) {
            Optional<GiftList> giftList = giftListRepository.findById(gift.getGiftList().getId());
            if (giftList.isPresent()) {
                gift.setGiftList(giftList.get());
                Gift savedGift = giftRepository.save(gift);
                return new GlobalResponseHandler().handleResponse("Gift created successfully",
                        savedGift, HttpStatus.CREATED, request);
            } else {
                return new GlobalResponseHandler().handleResponse("GiftList not found",
                        HttpStatus.BAD_REQUEST, request);
            }
        } else {
            return new GlobalResponseHandler().handleResponse("GiftList is required",
                    HttpStatus.BAD_REQUEST, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateGift(@PathVariable Long id, @RequestBody Gift gift, HttpServletRequest request) {
        Optional<Gift> existingGift = giftRepository.findById(id);
        if (existingGift.isPresent()) {
            Gift giftToUpdate = existingGift.get();
            giftToUpdate.setName(gift.getName());
            giftToUpdate.setDescription(gift.getDescription());
            giftToUpdate.setPrice(gift.getPrice());
            giftToUpdate.setImageUrl(gift.getImageUrl());
            
            if (gift.getGiftList() != null && gift.getGiftList().getId() != null) {
                Optional<GiftList> giftList = giftListRepository.findById(gift.getGiftList().getId());
                if (giftList.isPresent()) {
                    giftToUpdate.setGiftList(giftList.get());
                } else {
                    return new GlobalResponseHandler().handleResponse("GiftList not found",
                            HttpStatus.BAD_REQUEST, request);
                }
            }
            
            Gift savedGift = giftRepository.save(giftToUpdate);
            return new GlobalResponseHandler().handleResponse("Gift updated successfully",
                    savedGift, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Gift " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteGift(@PathVariable Long id, HttpServletRequest request) {
        Optional<Gift> foundGift = giftRepository.findById(id);
        if(foundGift.isPresent()) {
            giftRepository.deleteById(foundGift.get().getId());
            return new GlobalResponseHandler().handleResponse("Gift deleted successfully",
                    foundGift.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Gift " + id + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}