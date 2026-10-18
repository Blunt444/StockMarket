package stockmarket;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.lang.reflect.Type;

public class Storage {
    private static final Path file = Path.of(System.getProperty("user.home"), ".stockmarket", "data.json");
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new TimeAdapter())
            .setPrettyPrinting().create();

    public static class SaveData {
        public double balance;
        public Map<String, Holding> holdings = new TreeMap<>();
        public List<Statement> statements = new ArrayList<>();
        public Set<String> favs = new LinkedHashSet<>();
    }

    private static class TimeAdapter
            implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime src, Type t, JsonSerializationContext c) {
            return new JsonPrimitive(src.toString());
        }

        public LocalDateTime deserialize(JsonElement j, Type t, JsonDeserializationContext c) {
            return LocalDateTime.parse(j.getAsString());
        }
    }

    public static SaveData load() {
        if (!Files.exists(file)) {
            SaveData d = new SaveData();
            d.balance = 2000;
            return d;
        }

        try {
            SaveData d = gson.fromJson(Files.readString(file), SaveData.class);
            return d == null ? new SaveData() : d;
        } catch (IOException e) {
            System.out.println("Couldn't load data from save.");
            return new SaveData();
        }
    }

    public static void save(SaveData d) {
        try {
            Files.createDirectories(file.getParent());
            Path tmp = file.resolveSibling("data.json.tmp");
            Files.writeString(tmp, gson.toJson(d));
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Couldn't able to save data");
        }
    }
}
