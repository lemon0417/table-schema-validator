package com.example.customvalidator.controller;

import com.example.customvalidator.data.bo.UserBO;
import com.example.customvalidator.data.entity.User;
import com.example.customvalidator.data.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "使用者")
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserRepository repo;

    @ApiOperation(value = "查詢所有使用者")
    @GetMapping("/all")
    public List<User> findAll() {
        return repo.findAll();
    }

    @ApiOperation(value = "刪除全部使用者")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteAll() {
        repo.deleteAll();
    }

    @ApiOperation(value = "新增使用者1(for entity)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("add/test1")
    public void add(@Valid @RequestBody User user) {
        repo.save(
                new User(null
                        , user.getName()
                        , user.getAge()
                        , user.getAddress()
                        , user.getEmail()
                )
        );
    }

    @ApiOperation(value = "新增使用者2(for vo)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("add/test2")
    public void add2(@Valid @RequestBody UserBO bo) {
        repo.save(
                new User(null
                        , bo.getName()
                        , bo.getAge()
                        , bo.getAddress()
                        , bo.getEmail()
                )
        );
    }
}
