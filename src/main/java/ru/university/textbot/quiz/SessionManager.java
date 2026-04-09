package ru.university.textbot.quiz;

import ru.university.textbot.quiz.ElementDataLoader.ElementData;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    // HashMap: userId → UserSession
    private final Map<Long, UserSession> sessions = new HashMap<>();

    private final ElementData elementData;

    public SessionManager(ElementData elementData) {
        this.elementData = elementData;
    }

    public UserSession getOrCreate(long userId) {
        return sessions.computeIfAbsent(userId, id -> new UserSession(id, elementData));
    }

    public UserSession get(long userId) {
        return sessions.get(userId);
    }

    public void remove(long userId) {
        sessions.remove(userId);
    }

    public int size() {
        return sessions.size();
    }
}