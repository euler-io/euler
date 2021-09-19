package com.github.euler.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.typesafe.config.Config;

public class ConfigUtil {

    public static void set(Object obj, Config config) {
        config.root().forEach((path, configValue) -> {
            Object value = configValue.unwrapped();
            switch (configValue.valueType()) {
            case BOOLEAN:
                setBoolean(obj, path, value);
                break;
            case NUMBER:
                setNumber(obj, path, value);
                break;
            case STRING:
                set(obj, path, value, String.class);
                break;
            default:
                break;
            }
        });
    }

    private static void setNumber(Object obj, String path, Object value) {
        try {
            Method setter = getSetterSafe(obj, path, Float.class);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, Float.valueOf(((Number) value).floatValue()));
                return;
            }
            setter = getSetterSafe(obj, path, Float.TYPE);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, ((Number) value).floatValue());
                return;
            }

            setter = getSetterSafe(obj, path, Double.class);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, Double.valueOf(((Number) value).doubleValue()));
                return;
            }
            setter = getSetterSafe(obj, path, Double.TYPE);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, ((Number) value).doubleValue());
                return;
            }

            setter = getSetterSafe(obj, path, Long.class);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, Long.valueOf(((Number) value).longValue()));
                return;
            }
            setter = getSetterSafe(obj, path, Long.TYPE);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, ((Number) value).longValue());
                return;
            }

            setter = getSetterSafe(obj, path, Integer.class);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, Integer.valueOf(((Number) value).intValue()));
                return;
            }
            setter = getSetterSafe(obj, path, Integer.TYPE);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, ((Number) value).intValue());
                return;
            }

            setter = getSetterSafe(obj, path, Short.class);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, Short.valueOf(((Number) value).shortValue()));
                return;
            }
            setter = getSetter(obj, path, Short.TYPE);
            if (setter != null && value instanceof Number) {
                setter.invoke(obj, ((Number) value).shortValue());
                return;
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find setter for path " + path + " of type number.");
        }

    }

    private static void setBoolean(Object obj, String path, Object value) {
        try {
            Method setter = getSetterSafe(obj, path, Boolean.class);
            if (setter != null) {
                setter.invoke(obj, (Boolean) value);
            } else {
                setter = getSetter(obj, path, Boolean.TYPE);
                setter.invoke(obj, ((Boolean) value).booleanValue());
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find setter for path " + path + " of type boolean.");
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getSetterSafe(Object obj, String path, Class<?>... possibleClasses) {
        try {
            return getSetter(obj, path, possibleClasses);
        } catch (NoSuchMethodException e) {
            return null;
        }

    }

    private static void set(Object obj, String path, Object value, Class<?>... possibleClasses) {
        try {
            Method setter = getSetter(obj, path, possibleClasses);
            setter.invoke(obj, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getSetter(Object obj, String path, Class<?>... possibleClasses) throws NoSuchMethodException {
        String setterName = getSetterName(path);
        Class<?> objClass = obj.getClass();
        for (Class<?> valueClass : possibleClasses) {
            try {
                return objClass.getMethod(setterName, valueClass);
            } catch (NoSuchMethodException e) {
                // try next type
            }
        }
        String types = Arrays.stream(possibleClasses).map(t -> t.getName()).collect(Collectors.joining(" ,"));
        throw new NoSuchMethodException("Could not find setter " + setterName + " of any possible type: " + types);
    }

    private static String getSetterName(String path) {
        String name = Arrays.stream(path.split("-"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining());
        return "set" + name;
    }

}
