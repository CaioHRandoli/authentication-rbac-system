package com.project.login.repositories;

import com.project.login.enums.RoleEnum;
import com.project.login.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, UUID> {
    Optional<RoleModel> findByName(RoleEnum name);
    boolean existsByName(RoleEnum name);
}
