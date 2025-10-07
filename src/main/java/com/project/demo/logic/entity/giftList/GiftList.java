package com.project.demo.logic.entity.giftList;

import jakarta.persistence.*;

@Table(name = "gift_list")
@Entity
public class GiftList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    public GiftList(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public GiftList() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
