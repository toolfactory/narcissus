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

    /** The architecture type (x64, x86, arm64 or arm32). */
    public static final String archType;

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
            OS = OperatingSystem.BSD;
        } else if (osName.contains("nix") || osName.contains("aix")) {
            OS = OperatingSystem.Unix;
        } else {
            OS = OperatingSystem.Unknown;
        }

        // Determine architecture type. Note that ARM has to be distinguished from x86 before the word size is
        // consulted, otherwise a 32-bit ARM machine is labeled "x86", and the loader then tries to load an x86
        // library on an ARM machine.
        String osArch = null;
        try {
            osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ENGLISH);
        } catch (final SecurityException e) {
            // Ignore
        }
        String dataModel = null;
        try {
            dataModel = System.getProperty("sun.arch.data.model");
        } catch (final SecurityException e) {
            // Ignore
        }
        final boolean is32Bit = dataModel != null && dataModel.contains("32");
        if (osArch == null) {
            archType = is32Bit ? "x86" : "x64";
        } else if (osArch.equals("aarch64") || osArch.equals("arm64")) {
            archType = "arm64";
        } else if (osArch.startsWith("arm")) {
            // 32-bit ARM (armv7l, armhf, etc.) -- no library is built for this architecture, but naming it
            // correctly produces a clearer error than trying to load the x86 library
            archType = "arm32";
        } else if (is32Bit || (osArch.contains("86") && !osArch.contains("64")) || osArch.contains("32")) {
            archType = "x86";
        } else {
            archType = "x64";
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
        Throwable exception = null;
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
            if (!tempFileIsPosix) {
                // A non-POSIX filesystem (i.e. Windows) will not let the temp file be deleted while it is
                // mapped into this process, so delete any temp files left behind by previous JVMs instead.
                // A file that is still mapped into a running JVM simply fails to delete.
                deleteStaleTempFiles(tempFile.getParentFile(), baseName + "_", suffix, tempFile);
            }

            final byte[] buffer = new byte[8192];
            try (final OutputStream os = new FileOutputStream(tempFile)) {
                for (int readBytes; (readBytes = inputSream.read(buffer)) != -1;) {
                    os.write(buffer, 0, readBytes);
                }
            }

            // Load the library
            System.load(tempFile.getAbsolutePath());

            // Catch Throwable rather than Exception, since System.load() throws UnsatisfiedLinkError, which
            // would otherwise skip the deletion of the temp file below
        } catch (final Throwable t) {
            exception = t;
        }
        if (tempFile != null && (tempFileIsPosix || exception != null)) {
            // On POSIX filesystems the library stays mapped into the process after the file is unlinked, so
            // the temp file can be deleted as soon as it has been loaded. On other filesystems (i.e. Windows)
            // a mapped file cannot be deleted, so the temp file can only be deleted if the load failed, which
            // means nothing was mapped. (Deletion is best-effort in that case -- if it fails, the file is
            // deleted on exit, or swept as a stale temp file by the next JVM.)
            tempFile.delete();
        }
        if (exception != null) {
            throw new RuntimeException("Could not load library " + libraryResourcePath + " : " + exception,
                    exception);
        }
    }

    /**
     * Delete temp files extracted by previous JVM invocations, which could not be deleted at the time because the
     * library was still mapped into the process.
     *
     * @param tempDir
     *            the temporary directory
     * @param prefix
     *            the temp filename prefix
     * @param suffix
     *            the temp filename suffix
     * @param currentTempFile
     *            the temp file extracted by this JVM, which must not be deleted
     */
    private static void deleteStaleTempFiles(final File tempDir, final String prefix, final String suffix,
            final File currentTempFile) {
        try {
            if (tempDir == null) {
                return;
            }
            final File[] files = tempDir.listFiles();
            if (files == null) {
                return;
            }
            for (final File file : files) {
                final String name = file.getName();
                if (name.startsWith(prefix) && name.endsWith(suffix) && !file.equals(currentTempFile)) {
                    // Fails harmlessly if the file is still mapped into another running JVM
                    file.delete();
                }
            }
        } catch (final Exception e) {
            // Ignore -- sweeping stale temp files is best-effort
        }
    }
}
