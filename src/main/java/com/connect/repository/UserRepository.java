package com.connect.repository;

import java.util.List;
import java.util.Optional;

import com.connect.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.connect.model.User;

@Component
public class UserRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Repository method for making db queries and interacting with the database.

    public Optional<User> createUser(User user) {
        return Optional.of(mongoTemplate.insert(user)); // It returns the saved user else it returns null.
    }

    public Optional<User> findByEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
    }

    public Optional<User> findByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
    }

    public Optional<User> updateUserStatus(String username, UserStatus status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        Update update = new Update();
        update.set("status", status);
        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), User.class));
    }

    public Optional<List<User>> findAllUser() {
        return Optional.of(mongoTemplate.findAll(User.class));
    }
}
