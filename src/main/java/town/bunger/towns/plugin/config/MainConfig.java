package town.bunger.towns.plugin.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record MainConfig(
    @Setting("config-version")
    int configVersion,
    Debugging debugging
) {

    @ConfigSerializable
    public record Debugging(
        boolean log
    ) {}
}
