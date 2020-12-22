package com.example.customvalidator.data.repository;

import com.example.customvalidator.data.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
}
