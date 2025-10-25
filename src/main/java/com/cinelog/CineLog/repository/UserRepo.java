package com.cinelog.CineLog.repository;

import com.cinelog.CineLog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    public Optional<User> findByEmail(String email);
    public Optional<User> findById(Long id);
    public boolean existsById(Long id);
    public boolean existsByEmail(String email);


}
