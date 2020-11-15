package com.hedgehogsmind.springcouch2r.util;

// TODO @peter tests
// TODO @peter dos
public final class Couch2rPathUtil {

    public static String removeDoubleSlashes(final String path) {
        return path.replaceAll("(\\/{2,})", "/");
    }

    public static String normalizeWithTrailingSlash(final String path) {
        final String onlySingleSlashes = removeDoubleSlashes(path);
        return onlySingleSlashes.endsWith("/") ? onlySingleSlashes : onlySingleSlashes + "/";
    }

    public static String normalizeWithoutTrailingSlash(final String path) {
        final String onlySingleSlashes = removeDoubleSlashes(path);
        return onlySingleSlashes.endsWith("/") ?
                onlySingleSlashes.substring(0, onlySingleSlashes.length()-1) :
                onlySingleSlashes;
    }

}
