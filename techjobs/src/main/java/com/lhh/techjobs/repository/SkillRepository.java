package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
    @Query("SELECT s.name FROM Skill s")
    List<String> findAllSkillNames();
}
