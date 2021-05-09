package com.KuehneNagel.contactList.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.KuehneNagel.contactList.model.paging.Direction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public final class PeopleComparators {

    @EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<People>> map = new HashMap<>();

    static {
        map.put(new Key("name", Direction.asc), Comparator.comparing(People::getName));
        map.put(new Key("name", Direction.desc), Comparator.comparing(People::getName)
                                                           .reversed());
        
        map.put(new Key("position", Direction.asc), Comparator.comparing(People::getUrl));
        map.put(new Key("position", Direction.desc), Comparator.comparing(People::getUrl)
                                                               .reversed());        
    }

    public static Comparator<People> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }

    private PeopleComparators() {
    	
    }
}
