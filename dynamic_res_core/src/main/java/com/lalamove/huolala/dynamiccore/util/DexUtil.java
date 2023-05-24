package com.lalamove.huolala.dynamiccore.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: CloseUtil
 * @author: huangyuchen
 * @date: 4/16/22
 * @description:
 * @history:
 */
public class DexUtil {

    private DexUtil() {
    }

    public static final String TAG = DexUtil.class.getSimpleName();
    private static String PATH_LIST = "pathList";
    private static String NATIVE_LIBRARY_DIRECTORIES = "nativeLibraryDirectories";
    private static String SYSTEM_NATIVE_LIBRARY_DIRECTORIES = "systemNativeLibraryDirectories";
    private static String MAKE_PATH_ELEMENTS = "makePathElements";
    private static String NATIVE_LIBRARY_PATH_ELEMENTS = "nativeLibraryPathElements";

    /**
     * @param cl    classloader
     * @param soDir so存放路径
     * @throws Throwable
     */
    public static void installDexAndSo(ClassLoader cl, @Nullable File soDir) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            V26.install(cl, soDir);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            V25.install(cl, soDir);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            V23.install(cl, soDir);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            V19.install(cl, soDir);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            V14.install(cl, soDir);
        }
    }

    /**
     * Locates a given field anywhere in the class inheritance hierarchy.
     *
     * @param instance an object to search the field into.
     * @param name     field name
     * @return a field object
     * @throws NoSuchFieldException if the field cannot be located
     */
    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }


    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     *
     * @param instance       an object to search the method into.
     * @param name           method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Method "
                + name
                + " with parameters "
                + Arrays.asList(parameterTypes)
                + " not found in " + instance.getClass());
    }


    /**
     * Replace the value of a field containing a non null array, by a new array containing the
     * elements of the original array plus the elements of extraElements.
     *
     * @param instance      the instance whose field is to be modified.
     * @param fieldName     the field to modify.
     * @param extraElements elements to append at the end of the array.
     */
    private static void expandFieldArray(Object instance, String fieldName, Object[] extraElements)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field jlrField = findField(instance, fieldName);

        Object[] original = (Object[]) jlrField.get(instance);
        Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(), original.length + extraElements.length);

        // NOTE: changed to copy extraElements first, for patch load first

        System.arraycopy(extraElements, 0, combined, 0, extraElements.length);
        System.arraycopy(original, 0, combined, extraElements.length, original.length);

        jlrField.set(instance, combined);
    }

    private static final class V14 {
        private static void install(ClassLoader classLoader, File soFolder) throws Throwable {
            if (classLoader == null ||
                    soFolder == null ||
                    !soFolder.exists() ||
                    !soFolder.isDirectory()) {
                return;
            }
            Field pathListField = findField(classLoader, PATH_LIST);
            Object dexPathList = pathListField.get(classLoader);
            expandFieldArray(dexPathList, NATIVE_LIBRARY_DIRECTORIES, new File[]{soFolder});
        }

        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements =
                    findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class);

            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
        }
    }

    private static final class V19 {
        private static void install(ClassLoader classLoader, File soFolder) throws Throwable {
            if (classLoader == null ||
                    soFolder == null ||
                    !soFolder.exists() ||
                    !soFolder.isDirectory()) {
                return;
            }
            Field pathListField = findField(classLoader, PATH_LIST);
            Object dexPathList = pathListField.get(classLoader);
            expandFieldArray(dexPathList, NATIVE_LIBRARY_DIRECTORIES, new File[]{soFolder});
        }
    }

    private static final class V23 {
        private static void install(ClassLoader classLoader, File soFolder) throws Throwable {
            if (classLoader == null ||
                    soFolder == null ||
                    !soFolder.exists() ||
                    !soFolder.isDirectory()) {
                return;
            }
            Field pathListField = findField(classLoader, PATH_LIST);
            Object dexPathList = pathListField.get(classLoader);

            ArrayList<IOException> suppressedExceptions;

            Field nativeLibraryDirectories = findField(dexPathList, NATIVE_LIBRARY_DIRECTORIES);

            List<File> libDirs = (List<File>) nativeLibraryDirectories.get(dexPathList);
            libDirs.add(0, soFolder);
            Field systemNativeLibraryDirectories =
                    findField(dexPathList, SYSTEM_NATIVE_LIBRARY_DIRECTORIES);
            List<File> systemLibDirs = (List<File>) systemNativeLibraryDirectories.get(dexPathList);
            Method makePathElements =
                    findMethod(dexPathList, MAKE_PATH_ELEMENTS, List.class, File.class, List.class);
            suppressedExceptions = new ArrayList<>();
            libDirs.addAll(systemLibDirs);
            Object[] elements = (Object[]) makePathElements.
                    invoke(dexPathList, libDirs, null, suppressedExceptions);
            Field nativeLibraryPathElements = findField(dexPathList, NATIVE_LIBRARY_PATH_ELEMENTS);
            nativeLibraryPathElements.setAccessible(true);
            nativeLibraryPathElements.set(dexPathList, elements);
            if (!suppressedExceptions.isEmpty()) {
                for (IOException e : suppressedExceptions) {
                    Log.e(TAG, "Exception in makePathElement so", e);
                    throw e;
                }

            }

        }
    }

    private static final class V25 {
        private static void install(ClassLoader classLoader, File soFolder) throws Throwable {
            if (classLoader == null ||
                    soFolder == null ||
                    !soFolder.exists() ||
                    !soFolder.isDirectory()) {
                return;
            }
            Field pathListField = findField(classLoader, PATH_LIST);
            Object dexPathList = pathListField.get(classLoader);

            ArrayList<IOException> suppressedExceptions;


            suppressedExceptions = new ArrayList<>();
            Field nativeLibraryDirectories = findField(dexPathList, NATIVE_LIBRARY_DIRECTORIES);

            List<File> libDirs = (List<File>) nativeLibraryDirectories.get(dexPathList);
            libDirs.add(0, soFolder);
            Field systemNativeLibraryDirectories =
                    findField(dexPathList, SYSTEM_NATIVE_LIBRARY_DIRECTORIES);
            List<File> systemLibDirs = (List<File>) systemNativeLibraryDirectories.get(dexPathList);
            Method makePathElements =
                    findMethod(dexPathList, MAKE_PATH_ELEMENTS, List.class, File.class, List.class);
            libDirs.addAll(systemLibDirs);
            Object[] elements = (Object[]) makePathElements.
                    invoke(dexPathList, libDirs, null, suppressedExceptions);
            Field nativeLibraryPathElements = findField(dexPathList, NATIVE_LIBRARY_PATH_ELEMENTS);
            nativeLibraryPathElements.setAccessible(true);
            nativeLibraryPathElements.set(dexPathList, elements);
        }
    }

    private static final class V26 {
        private static void install(ClassLoader classLoader, File soFolder) throws Throwable {
            if (classLoader == null ||
                    soFolder == null ||
                    !soFolder.exists() ||
                    !soFolder.isDirectory()) {
                return;
            }
            Field pathListField = findField(classLoader, PATH_LIST);
            Object dexPathList = pathListField.get(classLoader);


            Field nativeLibraryDirectories = findField(dexPathList, NATIVE_LIBRARY_DIRECTORIES);

            List<File> libDirs = (List<File>) nativeLibraryDirectories.get(dexPathList);
            libDirs.add(0, soFolder);
            Field systemNativeLibraryDirectories =
                    findField(dexPathList, SYSTEM_NATIVE_LIBRARY_DIRECTORIES);
            List<File> systemLibDirs = (List<File>) systemNativeLibraryDirectories.get(dexPathList);
            Method makePathElements =
                    findMethod(dexPathList, MAKE_PATH_ELEMENTS, List.class);
            libDirs.addAll(systemLibDirs);
            Object[] elements = (Object[]) makePathElements.
                    invoke(dexPathList, libDirs);
            Field nativeLibraryPathElements = findField(dexPathList, NATIVE_LIBRARY_PATH_ELEMENTS);
            nativeLibraryPathElements.setAccessible(true);
            nativeLibraryPathElements.set(dexPathList, elements);

        }
    }
}
