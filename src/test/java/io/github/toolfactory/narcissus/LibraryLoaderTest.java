package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Tests {@link LibraryLoader}: operating system and architecture detection, and library extraction. */
@ExtendWith(TestMethodNameLogger.class)
public class LibraryLoaderTest {

    /** A resource that is on the classpath but is not a loadable native library. */
    private static final String NOT_A_LIBRARY = "/io/github/toolfactory/narcissus/Narcissus.class";

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testOperatingSystemDetection() {
        final String osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        if (osName.contains("mac") || osName.contains("darwin")) {
            assertThat(LibraryLoader.OS).isEqualTo(LibraryLoader.OperatingSystem.MacOSX);
        } else if (osName.contains("win")) {
            assertThat(LibraryLoader.OS).isEqualTo(LibraryLoader.OperatingSystem.Windows);
        } else if (osName.contains("nux")) {
            assertThat(LibraryLoader.OS).isEqualTo(LibraryLoader.OperatingSystem.Linux);
        }
        // A library is only built for the three operating systems above, so if the library loaded, the
        // operating system must be one of them
        assertThat(LibraryLoader.OS).isIn(LibraryLoader.OperatingSystem.MacOSX,
                LibraryLoader.OperatingSystem.Windows, LibraryLoader.OperatingSystem.Linux);
    }

    @Test
    public void testArchitectureDetection() {
        assertThat(LibraryLoader.archType).isIn("x64", "x86", "arm64", "arm32");
        final String osArch = System.getProperty("os.arch", "").toLowerCase(Locale.ENGLISH);
        if (osArch.equals("aarch64") || osArch.equals("arm64")) {
            assertThat(LibraryLoader.archType).isEqualTo("arm64");
        } else if (osArch.startsWith("arm")) {
            assertThat(LibraryLoader.archType).isEqualTo("arm32");
        } else if (osArch.equals("amd64") || osArch.equals("x86_64")) {
            assertThat(LibraryLoader.archType).isEqualTo("x64");
        }
    }

    @Test
    public void testTheLibraryForThisPlatformIsOnTheClasspath() throws Exception {
        final String extension = LibraryLoader.OS == LibraryLoader.OperatingSystem.MacOSX ? ".dylib"
                : LibraryLoader.OS == LibraryLoader.OperatingSystem.Windows ? ".dll" : ".so";
        final String osName = LibraryLoader.OS == LibraryLoader.OperatingSystem.MacOSX ? "macos"
                : LibraryLoader.OS == LibraryLoader.OperatingSystem.Windows ? "win" : "linux";
        final String resourcePath = "/lib/libnarcissus-" + osName + "-" + LibraryLoader.archType + extension;
        final InputStream inputStream = Narcissus.class.getResourceAsStream(resourcePath);
        assertThat(inputStream).as("library resource " + resourcePath).isNotNull();
        inputStream.close();
    }

    @Test
    public void testLoadLibraryFromJarWithNonexistentResource() {
        try {
            LibraryLoader.loadLibraryFromJar("/lib/no-such-library.so");
            fail("loadLibraryFromJar() should have thrown RuntimeException");
        } catch (final RuntimeException e) {
            assertThat(e.getMessage()).contains("no-such-library.so");
            assertThat(e.getCause()).isInstanceOf(FileNotFoundException.class);
        }
    }

    @Test
    public void testLoadLibraryFromJarWithResourceThatIsNotALibrary() {
        // System.load() throws UnsatisfiedLinkError, which is an Error rather than an Exception -- it must
        // still be wrapped in a RuntimeException, and the extracted temp file must still be deleted
        try {
            LibraryLoader.loadLibraryFromJar(NOT_A_LIBRARY);
            fail("loadLibraryFromJar() should have thrown RuntimeException");
        } catch (final RuntimeException e) {
            assertThat(e.getMessage()).contains("Narcissus.class");
            assertThat(e.getCause()).isInstanceOf(UnsatisfiedLinkError.class);
        }
        // Deletion is only guaranteed on a POSIX filesystem -- on Windows the delete can lose a race with a
        // virus scanner that still holds the file open, and the file is then only deleted on exit
        if (tempDirIsPosix()) {
            assertThat(listTempFiles("Narcissus_", ".class")).as("temp files left behind after a failed load")
                    .isEmpty();
        }
    }

    @Test
    public void testLoadLibraryFromJarAcceptsAPathWithoutALeadingSlash() {
        // The leading slash is optional
        try {
            LibraryLoader.loadLibraryFromJar(NOT_A_LIBRARY.substring(1));
            fail("loadLibraryFromJar() should have thrown RuntimeException");
        } catch (final RuntimeException e) {
            assertThat(e.getCause()).isInstanceOf(UnsatisfiedLinkError.class);
        }
    }

    @Test
    public void testDeleteStaleTempFiles() throws Exception {
        final Method deleteStaleTempFiles = Narcissus.findMethod(LibraryLoader.class, "deleteStaleTempFiles",
                File.class, String.class, String.class, File.class);

        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        final File stale = File.createTempFile("narcissus_stale_test_", ".tmp");
        final File current = File.createTempFile("narcissus_stale_test_", ".tmp");
        final File otherSuffix = File.createTempFile("narcissus_stale_test_", ".other");
        try {
            Narcissus.invokeStaticVoidMethod(deleteStaleTempFiles, tempDir, "narcissus_stale_test_", ".tmp",
                    current);
            // The stale file is deleted, but the current file and files with a different suffix are kept
            assertThat(stale).doesNotExist();
            assertThat(current).exists();
            assertThat(otherSuffix).exists();

            // A null temp directory is ignored rather than throwing
            Narcissus.invokeStaticVoidMethod(deleteStaleTempFiles, null, "narcissus_stale_test_", ".tmp", current);

            // A temp directory that is not a directory is ignored rather than throwing
            Narcissus.invokeStaticVoidMethod(deleteStaleTempFiles, current, "narcissus_stale_test_", ".tmp",
                    current);
        } finally {
            stale.delete();
            current.delete();
            otherSuffix.delete();
        }
    }

    /**
     * Test whether the temp directory is on a POSIX filesystem.
     *
     * @return true if the temp directory is on a POSIX filesystem
     */
    private static boolean tempDirIsPosix() {
        try {
            return new File(System.getProperty("java.io.tmpdir")).toPath().getFileSystem()
                    .supportedFileAttributeViews().contains("posix");
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * List the files in the temp directory whose name has the given prefix and suffix.
     *
     * @param prefix
     *            the filename prefix
     * @param suffix
     *            the filename suffix
     * @return the matching filenames
     */
    private static String[] listTempFiles(final String prefix, final String suffix) {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        final String[] names = tempDir.list();
        if (names == null) {
            return new String[0];
        }
        int numMatching = 0;
        for (int i = 0; i < names.length; i++) {
            if (names[i].startsWith(prefix) && names[i].endsWith(suffix)) {
                names[numMatching++] = names[i];
            }
        }
        final String[] matching = new String[numMatching];
        System.arraycopy(names, 0, matching, 0, numMatching);
        return matching;
    }
}
