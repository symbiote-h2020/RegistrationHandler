/*
 *  Copyright 2018 Atos
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
