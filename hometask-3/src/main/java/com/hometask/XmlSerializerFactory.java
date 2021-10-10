package com.hometask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import net.openhft.compiler.CompilerUtils;

public class XmlSerializerFactory<T> implements SerializerFactory<T> {
    private final Class<T> clazz;

    // source strings for methods that serialize all necessary classes
    private final Map<Class<?>, String> serializerMethods = new HashMap<>();
    private static final Set<Class<?>> PRIMITIVE_WRAPPER_TYPES = Set.of(Byte.class, Short.class, Integer.class,
            Long.class, Float.class, Double.class, Character.class, Boolean.class);

    public XmlSerializerFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    private static <U> boolean isImmediatelySerializable(Class<U> clazz) {
        return clazz.equals(String.class) || clazz.isPrimitive() || PRIMITIVE_WRAPPER_TYPES.contains(clazz);
    }

    private static String capitalizeFirstLetter(String s) {
        if (s == null) {
            return null;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String buildGetterNameForField(Field field) {
        Class<?> type = field.getType();
        String prefix = type.equals(Boolean.class) || type.equals(boolean.class) ? "is" : "get";
        return prefix + capitalizeFirstLetter(field.getName());
    }

    // search for valid getters recursively
    // returns a mapping from field to getter name
    private static <U> Map<Field, String> findAllGetters(Class<U> clazz) {
        if (clazz == null || clazz.equals(Object.class)) {
            return Collections.emptyMap();
        }
        Map<Field, String> fieldToGetterName = new HashMap<>(findAllGetters(clazz.getSuperclass()));
        for (Field field : clazz.getDeclaredFields()) {
            String getterName = buildGetterNameForField(field);
            try {
                Method getter = clazz.getMethod(getterName);
                fieldToGetterName.put(field, getterName);
            } catch (NoSuchMethodException ignored) {}
        }
        return fieldToGetterName;
    }

    private <U> void createSerializerMethodForClass(Class<U> clazz) {
        if (serializerMethods.containsKey(clazz)) {
            return;
        }
        List<Class<?>> serializerMethodsCalledRecursively = new ArrayList<>();

        String simpleClassName = clazz.getSimpleName();
        String fqClassName = clazz.getCanonicalName();
        StringBuilder sb = new StringBuilder();
        sb.append("    private String serialize").append(capitalizeFirstLetter(simpleClassName)).append("(").append(fqClassName).append(" o) {\n")
                .append("        StringBuilder sb = new StringBuilder();\n");
        var fieldToGetterName = findAllGetters(clazz);
        for (var entry : fieldToGetterName.entrySet()) {
            Field field = entry.getKey();
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String getterName = entry.getValue();
            sb.append("        sb.append(\"<").append(fieldName).append(">\").append(");
            String callToGetter = "o." + getterName + "()";
            if (isImmediatelySerializable(fieldType)) {
                sb.append("String.valueOf(").append(callToGetter).append(")");
            } else {
                sb.append(callToGetter).append(" == null ? null : ");
                sb.append("serialize").append(capitalizeFirstLetter(fieldType.getSimpleName())).append("(")
                        .append(callToGetter).append(")");
                serializerMethodsCalledRecursively.add(fieldType);

            }
            sb.append(").append(\"</").append(fieldName).append(">\");\n");
        }
        sb.append("        return sb.toString();\n    }\n\n");

        serializerMethods.put(clazz, sb.toString());
        for (var type : serializerMethodsCalledRecursively) {
            createSerializerMethodForClass(type);
        }
    }

    @Override
    public Serializer<T> createSerializer() {
        StringBuilder sb = new StringBuilder();

        String simpleClassName = clazz.getSimpleName();
        String fqClassName = clazz.getCanonicalName();
        String serializerClassName = simpleClassName + "ToXmlSerializer";

        sb.append("package com.hometask;\n\n");

        // class header
        sb.append("public class ").append(serializerClassName).append(" implements Serializer<")
                .append(fqClassName).append("> {\n");

        // generate source codes for methods that serialize all necessary classes
        // and save them to a hashmap
        createSerializerMethodForClass(clazz);

        for (var methodSource : serializerMethods.values()) {
            sb.append(methodSource);
        }

        // method header
        sb.append("    @Override\n    public String serialize(").append(fqClassName).append(" o").append(") {\n");
        sb.append("        return serialize").append(simpleClassName).append("(o);\n    }\n}\n");

        String serializerSource = sb.toString();

        try {
            return (Serializer<T>) CompilerUtils.CACHED_COMPILER
                    .loadFromJava("com.hometask." + serializerClassName, serializerSource).getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
