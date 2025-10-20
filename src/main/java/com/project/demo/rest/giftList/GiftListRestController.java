package com.project.demo.rest.giftList;

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

import java.util.Optional;

@RestController
@RequestMapping("/gift-lists")
public class GiftListRestController {
    @Autowired
    private GiftListRepository giftListRepository;

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


}
