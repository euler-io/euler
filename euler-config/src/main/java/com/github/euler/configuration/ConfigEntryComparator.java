package com.github.euler.configuration;

import java.util.Comparator;
import java.util.Map.Entry;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class ConfigEntryComparator implements Comparator<Entry<String, ConfigValue>> {

    @Override
    public int compare(Entry<String, ConfigValue> e1, Entry<String, ConfigValue> e2) {
        String o1 = getOrder(e1);
        String o2 = getOrder(e2);
        return o1.compareTo(o2);
    }

    private String getOrder(Entry<String, ConfigValue> c) {
        if (c instanceof ConfigObject && ((ConfigObject) c.getValue()).containsKey("order")) {
            return ((ConfigObject) c.getValue()).get("order").render();
        } else {
            return c.getKey();
        }
    }

}
