package com.connect.buffer;

import com.connect.model.Message;
import com.connect.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageBuffer {

    private static final int BATCH_SIZE = 10;

    private final List<Message> buffer = new ArrayList<>();

    @Autowired
    private RoomRepository roomRepository;

    // I am making it synchronized so that multiple threads cannot access it at the same time.
    public synchronized void addMessage(Message message) {
        System.out.println(message.toString());
        buffer.add(message);
        if (buffer.size() >= BATCH_SIZE) {
            flushBuffer();
        }
    }

    public List<Message> getMessages() {
        return buffer;
    }

    public int size() {
        return buffer.size();
    }

    public void flushBuffer() {
        if (!buffer.isEmpty()) {
            roomRepository.addMessages(buffer);
            buffer.clear(); // Clearing the buffer.
        }
    }

    // Manually flush method.
    public synchronized void flushIfNeeded() {
        flushBuffer();
    }

}
