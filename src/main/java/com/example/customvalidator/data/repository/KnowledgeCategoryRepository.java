package com.example.customvalidator.data.repository;

import com.example.customvalidator.data.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long> {
}
