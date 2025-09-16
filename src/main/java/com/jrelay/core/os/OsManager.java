package com.jrelay.core.os;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

import com.jrelay.core.models.Preference.Lang;
import com.jrelay.core.models.Preference.Theme;
import com.jthemedetecor.OsThemeDetector;
import com.jthemedetecor.util.OsInfo;

import lombok.Getter;
import lombok.ToString;

/**
 * Detects and stores environment-specific settings such as OS, UI theme, and
 * system language.
 * <p>
 * The {@code OsManager} class determines the operating system, preferred UI
 * theme,
 * and system language at runtime. This information can be used to adapt the
 * application's
 * behavior and appearance to the user's environment.
 * <p>
 * OS detection is based on the {@code os.name} system property, theme detection
 * uses
 * {@code OsThemeDetector}, and language detection relies on the system locale.
 * <p>
 * Supported OS values: {@code WIN}, {@code LINUX}, {@code MACOS}.
 * Supported themes: {@code DARK}, {@code LIGHT}.
 * <p>
 * This class is immutable once instantiated.
 * <p>
 *
 * @author @ASDG14N
 * @since 03-08-2025
 */
@ToString
public class OsManager {

    private static OsManager instance;

    private final String USER_HOME;

    @Getter
    private final Os operatingSystem;

    @Getter
    private final Theme theme;

    @Getter
    private final Lang language;

    /**
     * Constructs a new {@code OsManager}, performing OS, theme, and language
     * detection.
     */
    private OsManager() {
        this.operatingSystem = detectOs();
        this.theme = detectTheme();
        this.language = detectLanguage();
        this.USER_HOME = System.getProperty("user.home");
    }

    public static OsManager getInstance() {
        if (instance == null) {
            instance = new OsManager();
        }
        return instance;
    }

    /**
     * Detects the current operating system.
     *
     * @return the corresponding {@link Os} enum
     * @throws UnsupportedOperationException if the OS is not recognized
     */
    private Os detectOs() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows"))
            return Os.WIN;
        if (OsInfo.isLinux())
            return Os.LINUX;
        if (os.contains("mac"))
            return Os.MACOS;
        throw new UnsupportedOperationException("unsupported OS: " + os);
    }

    /**
     * Detects whether the system is using a dark or light UI theme.
     *
     * @return {@code Theme.DARK} if dark mode is active, otherwise
     * {@code Theme.LIGHT}
     */
    private Theme detectTheme() {
        return OsThemeDetector.getDetector().isDark() ? Theme.DARK : Theme.LIGHT;
    }

    private Lang detectLanguage() {
        String langCode = Locale.getDefault().getLanguage().toLowerCase();
        return switch (langCode) {
            case "es" -> Lang.ES;
            case "fr" -> Lang.FR;
            case "de" -> Lang.DE;
            case "it" -> Lang.IT;
            case "pt" -> Lang.PT;
            case "zh" -> Lang.ZH;
            case "ja" -> Lang.JA;
            case "ko" -> Lang.KO;
            default -> Lang.EN;
        };
    }

    public void putInClipboard(String contents) {
        StringSelection selection = new StringSelection(contents);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    /**
     * Writes the given JSON content to a file at the specified path.
     * <p>
     * If the file name is {@code null} or empty, an
     * {@link IllegalArgumentException} is thrown.
     * Ensures the file name ends with a {@code .json} extension. The file is
     * created if it does not exist,
     * or overwritten if it does.
     *
     * @param content  the JSON content to write
     * @param fileName the name (or path) of the target file
     * @throws IllegalArgumentException if {@code fileName} is {@code null} or blank
     * @author ASDFG14N
     * @since 07-08-2025
     */
    public void writeJsonToFile(String content, String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Error");
        }

        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }

        Path filePath = Path.of(fileName);
        try {
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a file from the specified source path to the target path.
     * <p>
     * If the target file already exists, it will be replaced.
     * If the parent directory of the target file does not exist, it will be
     * created.
     * </p>
     *
     * @param sourcePath the path of the source file
     * @param targetPath the path of the target file
     * @throws IllegalArgumentException if the source file does not exist or is not
     *                                  readable
     * @author ASDFG14N
     * @since 01-09-2025
     */
    public void copyFile(String sourcePath, String targetPath) {
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);

        if (!Files.exists(source) || !Files.isReadable(source)) {
            throw new IllegalArgumentException("The source file does not exist or is not readable: " + sourcePath);
        }

        Path parentDir = target.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String appDataDir() {
        String path = switch (operatingSystem) {
            case WIN -> System.getenv("APPDATA") + File.separator + "JRelay" + File.separator;
            case MACOS -> USER_HOME + "/Library/Application Support/JRelay/";
            default -> USER_HOME + File.separator + ".config" + File.separator + "JRelay" + File.separator;
        };
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("The directory could not be created: " + path);
        }
        return path;
    }


}
