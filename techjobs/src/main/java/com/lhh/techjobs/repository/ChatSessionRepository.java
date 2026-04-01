package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {
}
