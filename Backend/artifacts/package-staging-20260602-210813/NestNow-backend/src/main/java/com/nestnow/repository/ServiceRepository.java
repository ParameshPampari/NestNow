package com.nestnow.repository;

import com.nestnow.entity.Category;
import com.nestnow.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository
        extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByCategory(Category category);

    List<ServiceEntity> findByActiveTrue();

    List<ServiceEntity> findByActiveTrueAndCategory(Category category);

    List<ServiceEntity> findByActiveTrueAndTitleContainingIgnoreCase(
            String title
    );
}
