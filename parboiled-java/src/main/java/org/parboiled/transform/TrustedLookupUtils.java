package org.parboiled.transform;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class TrustedLookupUtils {
    private TrustedLookupUtils () {
        //hide default constructor
    }

    static final MethodHandles.Lookup TRUSTED_LOOKUP = getTrustedLookup();

    private static MethodHandles.Lookup getTrustedLookup() {
        try {
            final Class<?> unsafeType = getUnsafeType();
            final Unsafe unsafe = getUnsafeInstance(unsafeType);

            final Field trustedLookupField = getTrustedLookupField();
            final Object trustedLookupBase = getTrustedLookupBase(unsafeType, unsafe, trustedLookupField);
            final long trustedLookupOffset = getTrustedLookupOffset(unsafeType, unsafe, trustedLookupField);

            var getObjectMethod = unsafeType.getMethod("getObject", Object.class, long.class);
            return (MethodHandles.Lookup) getObjectMethod.invoke(unsafe, trustedLookupBase, trustedLookupOffset);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Class<?> getUnsafeType() throws ClassNotFoundException {
        return Class.forName("sun.misc.Unsafe");
    }

    private static Unsafe getUnsafeInstance(Class<?> unsafeType) throws NoSuchFieldException, IllegalAccessException {
        Field theUnsafeField = unsafeType.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        return (Unsafe) theUnsafeField.get(null);
    }

    private static Field getTrustedLookupField() throws NoSuchFieldException {
        return MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
    }

    private static Object getTrustedLookupBase(Class<?> unsafeType, Unsafe unsafe, Field trustedLookupField) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method baseMethod = unsafeType.getMethod("staticFieldBase", Field.class);
        return baseMethod.invoke(unsafe, trustedLookupField);
    }

    private static long getTrustedLookupOffset(Class<?> unsafeType, Unsafe unsafe, Field trustedLookupField) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method offsetMethod = unsafeType.getMethod("staticFieldOffset", Field.class);
        return (long) offsetMethod.invoke(unsafe, trustedLookupField);
    }
}
