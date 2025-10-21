package com.project.demo.rest.giftList;

import com.project.demo.logic.entity.gift.Gift;
import com.project.demo.logic.entity.gift.GiftRepository;
import com.project.demo.logic.entity.giftList.GiftList;
import com.project.demo.logic.entity.giftList.GiftListRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.order.Order;
import com.project.demo.logic.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gift-lists")
public class GiftListRestController {
    @Autowired
    private GiftListRepository giftListRepository;
    
    @Autowired
    private GiftRepository giftRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<GiftList> ordersPage = giftListRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Gift List retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    public ResponseEntity<?> addGiftList(@RequestBody GiftList gifList, HttpServletRequest request) {
        GiftList savedOrder = giftListRepository.save(gifList);
            return new GlobalResponseHandler().handleResponse("Gift list created successfully",
                    savedOrder, HttpStatus.CREATED, request);
    }

    @PutMapping
    public ResponseEntity<?> editGiftList(@RequestBody GiftList gifList, HttpServletRequest request) {
        GiftList savedOrder = giftListRepository.save(gifList);
        return new GlobalResponseHandler().handleResponse("Gift list created successfully",
                savedOrder, HttpStatus.CREATED, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id, HttpServletRequest request) {
        Optional<GiftList> foundItem = giftListRepository.findById(id);
        if(foundItem.isPresent()) {
            giftListRepository.deleteById(foundItem.get().getId());
            return new GlobalResponseHandler().handleResponse("Gift List deleted successfully",
                    foundItem.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Gift List " + id + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping("/{giftListId}/gifts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addGiftToGiftList(@PathVariable Long giftListId, @RequestBody Gift gift, HttpServletRequest request) {
        Optional<GiftList> foundGiftList = giftListRepository.findById(giftListId);
        if(foundGiftList.isPresent()) {
            GiftList giftList = foundGiftList.get();
            gift.setGiftList(giftList);
            Gift savedGift = giftRepository.save(gift);
            return new GlobalResponseHandler().handleResponse("Gift added to Gift List successfully",
                    savedGift, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Gift List " + giftListId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/{giftListId}/gifts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getGiftsFromGiftList(
            @PathVariable Long giftListId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Optional<GiftList> foundGiftList = giftListRepository.findById(giftListId);
        if(foundGiftList.isPresent()) {
            Pageable pageable = PageRequest.of(page-1, size);
            Page<Gift> giftsPage = giftRepository.findByGiftListId(giftListId, pageable);
            
            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(giftsPage.getTotalPages());
            meta.setTotalElements(giftsPage.getTotalElements());
            meta.setPageNumber(giftsPage.getNumber() + 1);
            meta.setPageSize(giftsPage.getSize());

            return new GlobalResponseHandler().handleResponse("Gifts from Gift List retrieved successfully",
                    giftsPage.getContent(), HttpStatus.OK, meta);
        } else {
            return new GlobalResponseHandler().handleResponse("Gift List " + giftListId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{giftListId}/gifts/{giftId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeGiftFromGiftList(@PathVariable Long giftListId, @PathVariable Long giftId, HttpServletRequest request) {
        Optional<GiftList> foundGiftList = giftListRepository.findById(giftListId);
        Optional<Gift> foundGift = giftRepository.findById(giftId);
        
        if(foundGiftList.isPresent() && foundGift.isPresent()) {
            Gift gift = foundGift.get();
            if(gift.getGiftList().getId().equals(giftListId)) {
                giftRepository.deleteById(giftId);
                return new GlobalResponseHandler().handleResponse("Gift removed from Gift List successfully",
                        gift, HttpStatus.OK, request);
            } else {
                return new GlobalResponseHandler().handleResponse("Gift " + giftId + " does not belong to Gift List " + giftListId,
                        HttpStatus.BAD_REQUEST, request);
            }
        } else {
            return new GlobalResponseHandler().handleResponse("Gift List or Gift not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

}
