package com.lhh.techjobs.repository;

import com.lhh.techjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    User findByEmail(String email);

    @Query("SELECT u.avatar FROM User u WHERE u.id = :id")
    String getAvatar(@Param("id") int id);
}
