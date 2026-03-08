package com.smirk.hysaga.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

public class JsonStorageManager {

    private static JsonStorageManager instance;
    private final Gson gson;

    public static JsonStorageManager getInstance() {
        if (instance == null) {
            instance = new JsonStorageManager();
        }
        return instance;
    }

    private JsonStorageManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
    }

    private static class InstantTypeAdapter extends TypeAdapter<Instant> {
        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return Instant.parse(in.nextString());
        }
    }

    public <T> T read(Path path, Class<T> tClass) throws IOException {
        if (path == null || tClass == null) throw new IllegalArgumentException("path or class null");
        if (!Files.exists(path)) return null;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, tClass);
        }
    }

    public void writeAtomic(Path path, Object value) throws IOException {
        if (path == null) throw new IllegalArgumentException("path null");

        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);

        Path tmp = path.resolveSibling(path.getFileName().toString() + ".tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            gson.toJson(value, writer);
        }

        try {
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public boolean delete(Path path) throws IOException {
        if (path == null) throw new IllegalArgumentException("path null");
        return Files.deleteIfExists(path);
    }
}
