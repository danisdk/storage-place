package ru.storageplace.db;

import org.flywaydb.core.Flyway;
import ru.storageplace.config.DatabaseConfig;

public final class MigrationRunner {

    private MigrationRunner() {
    }

    public static void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        DatabaseConfig.url(),
                        DatabaseConfig.user(),
                        DatabaseConfig.password()
                )
                .locations("classpath:ru/storageplace/db/migration")
                .load();

        flyway.migrate();
    }
}