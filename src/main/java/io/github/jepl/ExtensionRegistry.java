package io.github.jepl;

import io.github.jepl.annotation.Extension;
import io.github.jepl.annotation.ExtensionPoint;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ExtensionRegistry {
    private static final Map<String, Object> REGISTRY = new ConcurrentHashMap<>();

    private ExtensionRegistry() {}

    public static void registerExtension(Object extension) {
        assert extension != null;

        List<String> registryKeys = buildRegistryKeys(extension);
        registryKeys.forEach(registryKey -> REGISTRY.put(registryKey, extension));
    }

    public static void unregisterExtension(Object extension) {
        assert extension != null;
        buildRegistryKeys(extension).forEach(REGISTRY::remove);
    }

    public static <T> T getExtension(
            Class<T> extensionPointType, ExtensionCoordinate extensionCoordinate) {
        Assert.notNull(extensionPointType, "extensionPointType must not be null");
        Assert.notNull(extensionCoordinate, "extensionCoordinate must not be null");

        String extensionRegistryKey =
                buildExtensionRegistryKey(extensionPointType, extensionCoordinate.getKey());
        return _getExtension(extensionPointType, extensionRegistryKey);
    }

    public static <T> T getExtensionByKey(Class<T> extensionPointType, String extensionKey) {
        return _getExtension(
                extensionPointType, buildExtensionRegistryKey(extensionPointType, extensionKey));
    }

    private static <T> T _getExtension(Class<T> extensionPointType, String registryKey) {
        assert extensionPointType != null;
        assert registryKey != null;

        T extension = (T) REGISTRY.get(registryKey);
        if (extension != null) {
            return extension;
        }
        // 如果精确匹配找不到扩展点，则降级找上一级是否有扩展点直到root level为止
        final int pos = registryKey.lastIndexOf('.');
        if (pos == -1) {
            return null;
        }
        String fallbackKey = registryKey.substring(0, pos);
        return _getExtension(extensionPointType, fallbackKey);
    }

    private static <T> String buildExtensionRegistryKey(Class<T> extensionPointType, String key) {
        assert extensionPointType != null;
        assert key != null;

        return String.format("%s.%s", extensionPointType.getName(), key);
    }

    private static List<String> buildRegistryKeys(Object extension) {
        assert extension != null;

        Class<?> type = extension.getClass();
        Extension extensionAnnotation = AnnotationUtils.findAnnotation(type, Extension.class);
        if (extensionAnnotation == null) {
            return Collections.emptyList();
        }

        ExtensionPoint extensionPoint = AnnotationUtils.findAnnotation(type, ExtensionPoint.class);
        if (extensionPoint == null) {
            String errorMsg =
                    String.format(
                            "Extension class %s is not annotated with @%s",
                            type.getName(), ExtensionPoint.class.getName());
            throw new IllegalArgumentException(errorMsg);
        }

        List<Class<?>> extensionPointTypes = getExtensionPointTypes(type);
        return extensionPointTypes.stream()
                .map(e -> String.format("%s.%s", e.getName(), extensionAnnotation.key()))
                .collect(Collectors.toList());
    }

    private static List<Class<?>> getExtensionPointTypes(Class<?> type) {
        Set<Class<?>> extensionPointTypes = ClassUtils.getAllInterfacesForClassAsSet(type);
        if (CollectionUtils.isEmpty(extensionPointTypes)) {
            return Collections.emptyList();
        }

        return extensionPointTypes.stream()
                .filter(e -> AnnotationUtils.findAnnotation(e, ExtensionPoint.class) != null)
                .collect(Collectors.toList());
    }
}
