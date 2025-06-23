package com.project.demo.rest.team;

import com.project.demo.logic.entity.team.Team;
import com.project.demo.logic.entity.team.TeamRepository;
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
@RequestMapping("/teams")
public class TeamRestController {

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllTeams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Team> teamsPage = teamRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(teamsPage.getTotalPages());
        meta.setTotalElements(teamsPage.getTotalElements());
        meta.setPageNumber(teamsPage.getNumber() + 1);
        meta.setPageSize(teamsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Teams retrieved successfully",
                teamsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTeamById(@PathVariable Long teamId, HttpServletRequest request) {
        Optional<Team> foundTeam = teamRepository.findById(teamId);
        if (foundTeam.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Team retrieved successfully",
                    foundTeam.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Team id " + teamId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody Team team, HttpServletRequest request) {
        Team savedTeam = teamRepository.save(team);
        return new GlobalResponseHandler().handleResponse("Team created successfully",
                savedTeam, HttpStatus.CREATED, request);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<?> updateTeam(@PathVariable Long teamId, @RequestBody Team team, HttpServletRequest request) {
        Optional<Team> foundTeam = teamRepository.findById(teamId);
        if (foundTeam.isPresent()) {
            team.setId(foundTeam.get().getId());
            teamRepository.save(team);
            return new GlobalResponseHandler().handleResponse("Team updated successfully",
                    team, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Team id " + teamId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteTeam(@PathVariable Long teamId, HttpServletRequest request) {
        Optional<Team> foundTeam = teamRepository.findById(teamId);
        if (foundTeam.isPresent()) {
            teamRepository.deleteById(teamId);
            return new GlobalResponseHandler().handleResponse("Team deleted successfully",
                    foundTeam.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Team id " + teamId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}