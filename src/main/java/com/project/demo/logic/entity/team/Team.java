package com.project.demo.logic.entity.team;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String stadium;
    private Integer founded;
    private String coach;
    private Boolean isInClubsWorldCup;
    private String teamLogo;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Player> players;


    // Default constructor
    public Team() {
    }

    // Parameterized constructor
    public Team(String name, String stadium, Integer founded, String coach, Boolean isInClubsWorldCup, String teamLogo) {
        this.name = name;
        this.stadium = stadium;
        this.founded = founded;
        this.coach = coach;
        this.isInClubsWorldCup = isInClubsWorldCup;
        this.teamLogo = teamLogo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public Integer getFounded() {
        return founded;
    }

    public void setFounded(Integer founded) {
        this.founded = founded;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public Boolean getIsInClubsWorldCup() {
        return isInClubsWorldCup;
    }

    public void setIsInClubsWorldCup(Boolean isInClubsWorldCup) {
        this.isInClubsWorldCup = isInClubsWorldCup;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(String teamLogo) {
        this.teamLogo = teamLogo;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}