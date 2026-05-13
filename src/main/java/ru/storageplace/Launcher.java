package ru.storageplace;

import javafx.application.Application;
import ru.storageplace.db.MigrationRunner;
import ru.storageplace.dev.DevScenarioRunner;

import java.util.Arrays;

public class Launcher {
    public static void main(String[] args) {
        configureConsoleEncoding();
        if (hasArgument(args, "--migrate-only")) {
            MigrationRunner.migrate();
            System.out.println("Миграции успешно выполнены");
            return;
        }

        if (hasArgument(args, "--dev-test-income")) {
            MigrationRunner.migrate();
            new DevScenarioRunner().runIncomeScenario();
            return;
        }

        if (hasArgument(args, "--dev-test-outcome")) {
            MigrationRunner.migrate();
            new DevScenarioRunner().runOutcomeScenario();
            return;
        }

        if (hasArgument(args, "--dev-test-transfer")) {
            MigrationRunner.migrate();
            new DevScenarioRunner().runTransferScenario();
            return;
        }

        if (!hasArgument(args, "--skip-migrations")) {
            MigrationRunner.migrate();
        }

        Application.launch(StoragePlaceApplication.class, args);
    }

    private static boolean hasArgument(String[] args, String expectedArgument) {
        return Arrays.asList(args).contains(expectedArgument);
    }

    private static void configureConsoleEncoding() {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));
    }
}
