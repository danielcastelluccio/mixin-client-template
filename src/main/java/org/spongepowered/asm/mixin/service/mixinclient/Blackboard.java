package org.spongepowered.asm.mixin.service.mixinclient;

import org.spongepowered.asm.service.*;

import java.util.HashMap;
import java.util.Map;

public class Blackboard implements IGlobalPropertyService {

    private final Map<String, Object> board = new HashMap<>();

    static class Key implements IPropertyKey {

        private final String key;

        Key(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return this.key;
        }
    }

    @Override
    public IPropertyKey resolveKey(String name) {
        return new Key(name);
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) this.board.get(key.toString());
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        this.board.put(key.toString(), value);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return this.getProperty(key) == null ? defaultValue : this.getProperty(key);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return null;
    }

}
