package com.hometask1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class JsonParser {
    private static Object parseArray(String line) {
        List<Object> elements = new ArrayList<>();
        for (String element : line.split(",")) {
            element = element.trim();
            if (element.equals("null")) {
                elements.add(null);
                continue;
            }
            try {
                elements.add((Object) Long.parseLong(element));
            } catch (Exception e) {
                elements.add((Object) element);
            }
        }
        return elements.toArray(new Object[0]);
    }

    private static Object parse(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            return null;
        }
        if (line.equals("null")) {
            return null;
        }
        if (line.charAt(0) == '"') {
            return line.substring(1, line.length() - 1);
        }
        if (line.charAt(0) == '[') {
            return parseArray(line.substring(1, line.length() - 1));
        }
        return Long.parseLong(line);
    }

    public static JsonObject parseFile(String path) throws FileNotFoundException {
        JsonObject json = new JsonObject();
        Scanner sc = new Scanner(new FileReader(path));
        sc.nextLine(); // {
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            line = line.trim();
            if (line.equals("}")) {
                break;
            }
            if (line.charAt(line.length() - 1) == ',') {
                line = line.substring(0, line.length() - 1);
            }
            int colonIndex = line.indexOf(':');
            String key = (String) parse(line.substring(0, colonIndex));
            Object value = parse(line.substring(colonIndex + 1));
            json.put(key, value);
        }
        return json;
    }

    private static Object solve1(JsonObject json) {
        String type = json.getAs("clientType", String.class);
        switch (type) {
            case "INDIVIDUAL": {
                String name = json.getAs("name", String.class);
                Long inn = json.getAs("inn", Long.class);
                String address = json.getAs("address", String.class);
                return new Individual(name, inn, address);
            }
            case "LEGAL_ENTITY": {
                String name = json.getAs("name", String.class);
                Long inn = json.getAs("inn", Long.class);
                return new LegalEntity(name, inn);
            }
            case "HOLDING": {
                String name = json.getAs("name", String.class);
                Long inn = json.getAs("inn", Long.class);
                Long[] subsidiaries = (Long[]) Arrays.stream(json.getAs("subsidiaries", Long[].class)).map(x -> (Long) x).toArray();
                return new Holding(name, inn, subsidiaries);
            }
        }
        return null;
    }

    private static Object solve2(JsonObject json) {
        ClientType clientType = ClientType.valueOf(json.getAs("clientType", String.class));
        return clientType.createClient(json);
    }

    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
        int type = sc.nextInt();
        JsonObject json = parseFile(sc.nextLine());
        Object client = (type == 1 ? solve1(json) : solve2(json));
        System.out.println(client.toString());
    }
}
