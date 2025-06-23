package com.project.demo.rest.team;

import com.project.demo.logic.entity.team.Player;
import com.project.demo.logic.entity.team.PlayerRepository;
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
@RequestMapping("/players")
public class PlayerRestController {

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllPlayers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Player> playersPage = playerRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(playersPage.getTotalPages());
        meta.setTotalElements(playersPage.getTotalElements());
        meta.setPageNumber(playersPage.getNumber() + 1);
        meta.setPageSize(playersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Players retrieved successfully",
                playersPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{playerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPlayerById(@PathVariable Long playerId, HttpServletRequest request) {
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (foundPlayer.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Player retrieved successfully",
                    foundPlayer.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Player id " + playerId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping
    public ResponseEntity<?> createPlayer(@RequestBody Player player, HttpServletRequest request) {
        Player savedPlayer = playerRepository.save(player);
        return new GlobalResponseHandler().handleResponse("Player created successfully",
                savedPlayer, HttpStatus.CREATED, request);
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<?> updatePlayer(@PathVariable Long playerId, @RequestBody Player player, HttpServletRequest request) {
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (foundPlayer.isPresent()) {
            player.setId(foundPlayer.get().getId());
            playerRepository.save(player);
            return new GlobalResponseHandler().handleResponse("Player updated successfully",
                    player, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Player id " + playerId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{playerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deletePlayer(@PathVariable Long playerId, HttpServletRequest request) {
        Optional<Player> foundPlayer = playerRepository.findById(playerId);
        if (foundPlayer.isPresent()) {
            playerRepository.deleteById(playerId);
            return new GlobalResponseHandler().handleResponse("Player deleted successfully",
                    foundPlayer.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Player id " + playerId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}