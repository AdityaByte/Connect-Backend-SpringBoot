package com.connect.repository;

import com.connect.model.Message;
import com.connect.model.Room;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<Room> addRoom(Room room) {
        return Optional.of(mongoTemplate.insert(room));
    }

    public Optional<List<Room>> getRooms() {
        Query query = new Query();
        query.fields()
                .include("roomId")
                .include("roomName")
                .include("date")
                .include("admin")
                .include("roomDescription");
        return Optional.of(mongoTemplate.find(query, Room.class));
    }

    public Optional<Room> findRoomByID(ObjectId roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Room.class));
    }

    public Optional<Room> findRoomByName(String roomName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomName").is(roomName));
        return Optional.ofNullable(mongoTemplate.findOne(query, Room.class));
    }

    public Optional<Message> addMessageToRoom(Message message) {
        return Optional.ofNullable(mongoTemplate.insert(message));
    }

    public Optional<List<Message>> getRoomSpecificMessages(ObjectId roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        return Optional.of(mongoTemplate.find(query, Message.class));
    }

}
