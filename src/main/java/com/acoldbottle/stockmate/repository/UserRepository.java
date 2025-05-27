package com.acoldbottle.stockmate.repository;

import com.acoldbottle.stockmate.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
