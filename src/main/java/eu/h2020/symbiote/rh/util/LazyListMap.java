package eu.h2020.symbiote.rh.util;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.map.LazyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LazyListMap<K, V> extends LazyMap<K, List<V>> {


    public LazyListMap(Map<K, List<V>> map) {
        super(map, new Factory<List<V>>() {
            @Override
            public List<V> create() {
                return new ArrayList<>();
            }
        });
    }

    public LazyListMap() {
        this(new HashMap<>());
    }
}
