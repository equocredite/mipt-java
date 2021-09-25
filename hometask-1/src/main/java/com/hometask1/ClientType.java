package com.hometask1;

import java.util.Arrays;

public enum ClientType {
    INDIVIDUAL {
        @Override
        public Object createClient(JsonObject json) {
            String name = json.getAs("name", String.class);
            Long inn = json.getAs("inn", Long.class);
            String address = json.getAs("address", String.class);
            return new Individual(name, inn, address);
        }
    },

    LEGAL_ENTITY {
        @Override
        public Object createClient(JsonObject json) {
            String name = json.getAs("name", String.class);
            Long inn = json.getAs("inn", Long.class);
            return new LegalEntity(name, inn);
        }
    },

    HOLDING {
        @Override
        public Object createClient(JsonObject json) {
            String name = json.getAs("name", String.class);
            Long inn = json.getAs("inn", Long.class);
            Long[] subsidiaries = (Long[]) Arrays.stream(json.getAs("subsidiaries", Long[].class)).map(x -> (Long) x).toArray();
            return new Holding(name, inn, subsidiaries);
        }
    };

    public abstract Object createClient(JsonObject json);
}
