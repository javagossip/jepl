package io.github.jepl;

import io.github.jepl.annotation.Extension;
import io.github.jepl.annotation.ExtensionPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtensionRegistryTest {

    @Test
    void registerExtension() {
        ExtensionRegistry.registerExtension(new ValidatorOne());
        ExtensionRegistry.registerExtension(new ValidatorTwo());
        ExtensionRegistry.registerExtension(new DefaultValidator());

        Validator validator = ExtensionRegistry.getExtensionByKey(Validator.class, "pay.one");
        assertNotNull(validator);
        validator = ExtensionRegistry.getExtensionByKey(Validator.class, "pay.three");
        assertNotNull(validator);
        assertTrue(validator instanceof DefaultValidator);
    }

    @Test
    void unregisterExtension() {
        ExtensionRegistry.registerExtension(new ValidatorOne());
        Validator validator = ExtensionRegistry.getExtensionByKey(Validator.class, "pay.one");
        assertNotNull(validator);

        ExtensionRegistry.unregisterExtension(validator);
        validator = ExtensionRegistry.getExtensionByKey(Validator.class, "pay.one");
        assertNull(validator);
    }

    @Test
    void getExtension() {
        ExtensionRegistry.registerExtension(new ValidatorOne());
        Validator validator =
                ExtensionRegistry.getExtension(
                        Validator.class, TestExtensionCoordinate.forKey("pay.one"));
        assertNotNull(validator);
    }

    @Test
    void getExtensionByKey() {
        ExtensionRegistry.registerExtension(new ValidatorOne());
        Validator validator = ExtensionRegistry.getExtensionByKey(Validator.class, "pay.one");
        assertNotNull(validator);

        validator = ExtensionRegistry.getExtensionByKey(Validator.class, "pay.three");
        assertNull(validator);
    }

    @ExtensionPoint
    interface Validator {
        void validate(Object t);
    }

    @Extension(key = "pay.one")
    static class ValidatorOne implements Validator {

        @Override
        public void validate(Object t) {}
    }

    @Extension(key = "pay.two")
    static class ValidatorTwo implements Validator {

        @Override
        public void validate(Object t) {}
    }

    @Extension(key = "pay")
    static class DefaultValidator implements Validator {

        @Override
        public void validate(Object t) {}
    }

    static class TestExtensionCoordinate implements ExtensionCoordinate {
        private final String key;

        private TestExtensionCoordinate(String key) {
            this.key = key;
        }

        public static TestExtensionCoordinate forKey(String key) {
            return new TestExtensionCoordinate(key);
        }

        @Override
        public String getKey() {
            return key;
        }
    }
}
