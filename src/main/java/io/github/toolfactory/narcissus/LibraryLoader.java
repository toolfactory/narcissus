/*
 * This file is part of Narcissus.
 *
 * Hosted at: https://github.com/toolfactory/narcissus
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.toolfactory.narcissus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * The Class LibraryLoader.
 */
class LibraryLoader {
    /** The operating system type. */
    public static final OperatingSystem OS;

    /** The machine word size. */
    public static int archBits;

    /** The architecture name (empty except on Mac) */
    public static String archName = "";

    /** The operating system type. */
    enum OperatingSystem {
        /** Windows. */
        Windows,

        /** Mac OS X. */
        MacOSX,

        /** Linux. */
        Linux,

        /** Solaris. */
        Solaris,

        /** BSD. */
        BSD,

        /** Unix or AIX. */
        Unix,

        /** Unknown. */
        Unknown
    }

    static {
        String osName = null;
        try {
            osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        } catch (final SecurityException e) {
            // Ignore
        }
        if (osName == null) {
            OS = OperatingSystem.Unknown;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            OS = OperatingSystem.MacOSX;
        } else if (osName.contains("win")) {
            OS = OperatingSystem.Windows;
        } else if (osName.contains("nux")) {
            OS = OperatingSystem.Linux;
        } else if (osName.contains("sunos") || osName.contains("solaris")) {
            OS = OperatingSystem.Solaris;
        } else if (osName.contains("bsd")) {
            OS = OperatingSystem.Unix;
        } else if (osName.contains("nix") || osName.contains("aix")) {
            OS = OperatingSystem.Unix;
        } else {
            OS = OperatingSystem.Unknown;
        }

        if (OS == OperatingSystem.MacOSX && "aarch64".equals(System.getProperty("os.arch"))) {
            archName = "arm";
        }

        archBits = 64;
        final String dataModel = System.getProperty("sun.arch.data.model");
        if (dataModel != null && dataModel.contains("32")) {
            archBits = 32;
        } else {
            final String osArch = System.getProperty("os.arch");
            if (osArch != null && ((osArch.contains("86") && !osArch.contains("64")) || osArch.contains("32"))) {
                archBits = 32;
            }
        }
    }

    /**
     * Load library from jar.
     *
     * @param libraryResourcePath
     *            the library resource path
     */
    static void loadLibraryFromJar(final String libraryResourcePath) {
        File tempFile = null;
        boolean tempFileIsPosix = false;
        Exception exception = null;
        try (InputStream inputSream = Narcissus.class.getResourceAsStream(
                libraryResourcePath.startsWith("/") ? libraryResourcePath : "/" + libraryResourcePath)) {
            if (inputSream == null) {
                throw new FileNotFoundException("Could not find library within jar: " + libraryResourcePath);
            }

            // Extract library to temp file
            final String filename = libraryResourcePath.substring(libraryResourcePath.lastIndexOf('/') + 1);
            final int dotIdx = filename.indexOf('.');
            final String baseName = dotIdx < 0 ? filename : filename.substring(0, dotIdx);
            final String suffix = dotIdx < 0 ? ".so" : filename.substring(dotIdx);
            tempFile = File.createTempFile(baseName + "_", suffix);
            tempFile.deleteOnExit();

            try {
                if (tempFile.toPath().getFileSystem().supportedFileAttributeViews().contains("posix")) {
                    tempFileIsPosix = true;
                }
            } catch (final Exception e) {
                // Ignore
            }

            final byte[] buffer = new byte[8192];
            try (final OutputStream os = new FileOutputStream(tempFile)) {
                for (int readBytes; (readBytes = inputSream.read(buffer)) != -1;) {
                    os.write(buffer, 0, readBytes);
                }
            }

            // Load the library
            System.load(tempFile.getAbsolutePath());

        } catch (final Exception e) {
            exception = e;
        }
        if (tempFile != null && tempFileIsPosix) {
            tempFile.delete();
        }
        if (exception != null) {
            throw new RuntimeException("Could not load library " + libraryResourcePath + " : " + exception);
        }
    }
}
