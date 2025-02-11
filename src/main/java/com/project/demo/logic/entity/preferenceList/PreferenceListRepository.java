package com.project.demo.logic.entity.preferenceList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceListRepository extends JpaRepository<PreferenceList, Long> {
}
