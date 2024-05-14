package com.project.demo.logic.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long>  {
    @Query("SELECT u FROM AppUser u WHERE LOWER(u.username) LIKE %?1%")
    List<AppUser> findUsersWithCharacterInName(String character);

    @Query("SELECT u FROM AppUser u WHERE u.username = ?1")
    Optional<AppUser> findByName(String name);
}
