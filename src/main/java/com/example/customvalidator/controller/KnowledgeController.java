package com.example.customvalidator.controller;

import com.example.customvalidator.data.bo.KnowledgeCategoryBO;
import com.example.customvalidator.data.entity.Knowledge;
import com.example.customvalidator.data.entity.KnowledgeCategory;
import com.example.customvalidator.data.repository.KnowledgeCategoryRepository;
import com.example.customvalidator.data.repository.KnowledgeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "知識點")
@RequestMapping("/knowledge")
@RestController
public class KnowledgeController {
    @Autowired
    private KnowledgeCategoryRepository categoryRepo;
    @Autowired
    private KnowledgeRepository repo;

    @ApiOperation(value = "查詢所有知識點")
    @GetMapping("/all")
    public List<KnowledgeCategory> findAll() {
        return categoryRepo.findAll();
    }

    @ApiOperation(value = "刪除全部知識點")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteAll() {
        repo.deleteAll();
        categoryRepo.deleteAll();
    }

    @ApiOperation(value = "新增知識點(for nested vo)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("add/test1")
    public void add(@Valid @RequestBody KnowledgeCategoryBO bo) {
        KnowledgeCategory category = categoryRepo.save(
                new KnowledgeCategory(
                        null
                        , bo.getName()
                )
        );
        repo.save(
                new Knowledge(
                        null
                        , category.getId()
                        , bo.getKnowledge().getName()
                        , bo.getKnowledge().getOrder()
                        , bo.getKnowledge().getRule()
                )
        );
    }
}
