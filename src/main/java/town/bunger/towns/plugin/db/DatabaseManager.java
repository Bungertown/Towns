package town.bunger.towns.plugin.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import town.bunger.towns.plugin.BungerTownsPlugin;
import town.bunger.towns.plugin.config.DatabaseConfig;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Driver;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

public class DatabaseManager {

    private static final Pattern MATCH_ALL_EXCEPT_INFORMATION_SCHEMA = Pattern.compile("^(?!INFORMATION_SCHEMA)(.*?)$");
    private static final Pattern MATCH_ALL = Pattern.compile("^(.*?)$");

    private final Configuration configuration;
    private final DataSource pool;

    public DatabaseManager(DatabaseConfig config, BungerTownsPlugin plugin) {
        // Disable jOOQ logo and tips
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
        // Load drivers
        forceLoadDrivers(this.getClass().getClassLoader());

        final SQLDialect dialect = config.type().dialect();
        plugin.getLogger().info("Using database dialect: " + dialect);

        final Settings settings = new Settings()
            .withMapRecordComponentParameterNames(true)
            .withRenderSchema(false)
            .withRenderQuotedNames(RenderQuotedNames.ALWAYS)
            .withRenderMapping(new RenderMapping()
                .withSchemata(
                    new MappedSchema()
                        .withInputExpression(MATCH_ALL_EXCEPT_INFORMATION_SCHEMA)
                        .withTables(new MappedTable()
                            .withInputExpression(MATCH_ALL)
                            .withOutput(config.tablePrefix() + "$0"))
                ));

        this.pool = new HikariDataSource(getPoolConfig(config, plugin));

        this.configuration = new DefaultConfiguration()
            .set(dialect)
            .set(settings)
            .set(this.pool)
            .set(ForkJoinPool.commonPool());
    }

    public DSLContext ctx() {
        return DSL.using(this.configuration);
    }

    public void migrate(DatabaseConfig config, BungerTownsPlugin plugin) {
        final String migrations = switch (config.type()) {
            case POSTGRESQL -> "town/bunger/towns/plugin/db/postgresql/";
            case MYSQL -> "town/bunger/towns/plugin/db/mysql/";
            case SQLITE -> "town/bunger/towns/plugin/db/sqlite/";
            case H2 -> "town/bunger/towns/plugin/db/h2/";
        };

        final var placeholders = Map.of(
            "tablePrefix", config.tablePrefix()
        );

        final var flyway = Flyway.configure(this.getClass().getClassLoader())
            .baselineVersion("0")
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .validateMigrationNaming(true)
            .dataSource(this.pool)
            .placeholders(placeholders)
            .locations(migrations)
            .table(config.tablePrefix() + "schema_history")
            .load();

        plugin.getLogger().info("Checking if the database needs repairs...");
        flyway.repair();
        plugin.getLogger().info("Checking for necessary database migrations...");
        flyway.migrate();
    }

    private static HikariConfig getPoolConfig(DatabaseConfig config, BungerTownsPlugin plugin) {
        final var poolConfig = new HikariConfig();
        if (config.type().isFileBased()) {
            final Path dbPath = plugin.getDataFolder().toPath().resolve(config.fileBased().name());
            poolConfig.setJdbcUrl("jdbc:" + config.type().name().toLowerCase() + ":./" + dbPath);
        } else {
            final DatabaseConfig.Server server = config.serverBased();
            poolConfig.setJdbcUrl("jdbc:" + config.type().name().toLowerCase() + "://" + server.host() + ":" + server.port() + "/" + server.database());
            poolConfig.setUsername(server.username());
            poolConfig.setPassword(server.password());
        }
        return poolConfig;
    }

    /**
     * Make sure all drivers are loaded where we can find them.
     */
    private static void forceLoadDrivers(final ClassLoader loader) {
        ServiceLoader.load(Driver.class, loader).stream().forEach(provider -> {
            try {
                Class.forName(provider.type().getName(), true, provider.type().getClassLoader());
            } catch (final ClassNotFoundException e) {
                throw new AssertionError(e);
            }
        });
    }
}
