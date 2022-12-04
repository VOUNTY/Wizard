package net.vounty.wizard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OptionSet {

    private final Map<String, Object> map;
    private final Gson gson;

    OptionSet(List<String> arguments) {
        this.map = new LinkedHashMap<>();
        this.gson = new GsonBuilder().setLenient().serializeNulls().create();
        for (final var argument : arguments) {
            final var values = argument.split("=");
            if (values.length == 2) {
                final var key = values[0];
                final var object = values[1];
                this.map.put(key, object);
            }
        }
    }

    public void setOption(String key, Object value) {
        this.map.put(key, value);
    }

    public void setOption(String key, Object value, Boolean when) {
        if (when)
            this.map.put(key, value);
    }

    public Boolean hasOption(String key) {
        return this.map.containsKey(key);
    }

    public <Type> Type getOption(String key, Class<Type> clazz) {
        return this.gson.fromJson(this.gson.toJson(this.map.get(key)), clazz);
    }

    public <Type> Type getOption(String key, Class<Type> clazz, Type type) {
        return this.hasOption(key) ? this.getOption(key, clazz) : type;
    }

    public Object getOption(String key) {
        return this.map.get(key);
    }

    public Object getOption(String key, Object object) {
        return this.map.getOrDefault(key, object);
    }

    public static OptionSet of(List<String> arguments) {
        return new OptionSet(arguments);
    }

    public static OptionSet empty() {
        return new OptionSet(new LinkedList<>());
    }

}
