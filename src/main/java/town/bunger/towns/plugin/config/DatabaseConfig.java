package town.bunger.towns.plugin.config;

import org.jooq.SQLDialect;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record DatabaseConfig(
    @Setting("config-version")
    int configVersion,
    Type type,
    @Setting("table-prefix")
    String tablePrefix,
    @Setting("file-based")
    File fileBased,
    @Setting("server-based")
    Server serverBased
) {

    public enum Type {
        POSTGRESQL,
        MYSQL,
        SQLITE,
        H2;

        public boolean isFileBased() {
            return switch (this) {
                case SQLITE, H2 -> true;
                default -> false;
            };
        }

        public SQLDialect dialect() {
            return switch (this) {
                case POSTGRESQL -> SQLDialect.POSTGRES;
                case MYSQL -> SQLDialect.MYSQL;
                case SQLITE -> SQLDialect.SQLITE;
                case H2 -> SQLDialect.H2;
            };
        }
    }

    @ConfigSerializable
    public record File(
        String name
    ) {
    }

    @ConfigSerializable
    public record Server(
        String host,
        int port,
        String database,
        String username,
        String password
    ) {
    }
}
