package com.gila.notification_service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gila.notification_service.dto.UserDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class UserService {
    private List<UserDto> cachedUsers;

    @PostConstruct
    public void init() throws IOException {
        String path = "/db/users_seed.json";
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream(path);
        this.cachedUsers = mapper.readValue(is, new TypeReference<List<UserDto>>() {});
    }

    public List<UserDto> getAllMockUsers() {
        return cachedUsers;
    }

    public List<UserDto> findSubscribers(String category) {
        return cachedUsers.stream()
                .filter(u -> u.subscribed().contains(category))
                .toList();
    }
}