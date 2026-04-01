package com.lhh.techjobs.repository;

import com.lhh.techjobs.dto.response.MessageResponse;
import com.lhh.techjobs.entity.ChatSession;
import com.lhh.techjobs.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT CONCAT(m.sender, ': ', m.content) " +
            "FROM Message m " +
            "WHERE m.chatSession = :chatSession " +
            "ORDER BY m.id DESC")
    List<String> findTop5Message(@Param("chatSession") ChatSession chatSession , Pageable pageable);

    @Query("SELECT new com.lhh.techjobs.dto.response.MessageResponse(m.content, m.createdAt, m.sender) " +
            "FROM Message m " +
            "WHERE m.chatSession = :chatSession " +
            "ORDER BY m.id DESC")
    List<MessageResponse> findTop10RecentMessage(@Param("chatSession") ChatSession chatSession , Pageable pageable);
}
