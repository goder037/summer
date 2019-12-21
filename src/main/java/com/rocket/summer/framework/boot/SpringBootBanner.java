package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.boot.ansi.AnsiColor;
import com.rocket.summer.framework.boot.ansi.AnsiOutput;
import com.rocket.summer.framework.boot.ansi.AnsiStyle;
import com.rocket.summer.framework.core.env.Environment;

import java.io.PrintStream;

/**
 * Default Banner implementation which writes the 'Spring' banner.
 *
 * @author Phillip Webb
 */
class SpringBootBanner implements Banner {

    private static final String[] BANNER = { "",
            "  .   ____          _            __ _ _",
            " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\",
            "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\",
            " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )",
            "  '  |____| .__|_| |_|_| |_\\__, | / / / /",
            " =========|_|==============|___/=/_/_/_/" };

    private static final String SPRING_BOOT = " :: Spring Boot :: ";

    private static final int STRAP_LINE_SIZE = 42;

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass,
                            PrintStream printStream) {
        for (String line : BANNER) {
            printStream.println(line);
        }
        String version = SpringBootVersion.getVersion();
        version = (version != null) ? " (v" + version + ")" : "";
        String padding = "";
        while (padding.length() < STRAP_LINE_SIZE
                - (version.length() + SPRING_BOOT.length())) {
            padding += " ";
        }

        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT,
                AnsiColor.DEFAULT, padding, AnsiStyle.FAINT, version));
        printStream.println();
    }

}

