package com.hedgehogsmind.springcouchrest.util;

public final class PathUtil {

    /**
     * Removes multiple slashes by single one.
     *
     * @param path Path to modify.
     * @return Path without double slashes.
     */
    public static String removeMultipleSlashes(final String path) {
        return path.replaceAll("(\\/{2,})", "/");
    }

    /**
     * First multiple slashes are removed and then a trailing slash is appended if not present yet.
     * <b>Adds trailing slash only if the path is not empty!</b>
     *
     * @param path Path to modify.
     * @return Path without multiple slashes and with trailing slash.
     */
    public static String normalizeWithTrailingSlash(final String path) {
        if ( path.isEmpty() ) return "";

        final String onlySingleSlashes = removeMultipleSlashes(path);

        return onlySingleSlashes.endsWith("/") ?
                onlySingleSlashes :
                onlySingleSlashes + "/";
    }

    /**
     * First multiple slashes are removed and then any a trailing slash is removed if present.
     *
     * @param path Path to modify.
     * @return Path without multiple slashes and without trailing slash.
     */
    public static String normalizeWithoutTrailingSlash(final String path) {
        final String onlySingleSlashes = removeMultipleSlashes(path);
        return onlySingleSlashes.endsWith("/") ?
                onlySingleSlashes.substring(0, onlySingleSlashes.length()-1) :
                onlySingleSlashes;
    }

}
