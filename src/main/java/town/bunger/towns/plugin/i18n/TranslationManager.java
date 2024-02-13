package town.bunger.towns.plugin.i18n;

import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import town.bunger.towns.plugin.BungerTownsPlugin;

import java.util.Locale;
import java.util.ResourceBundle;

public final class TranslationManager {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final TranslationRegistry REGISTRY = TranslationRegistry.create(BungerTownsPlugin.key("translations"));

    private static boolean registered = false;

    public static void register() {
        if (registered) {
            throw new IllegalStateException("Already registered translations");
        }

        REGISTRY.defaultLocale(DEFAULT_LOCALE);

        final ResourceBundle bundle = ResourceBundle.getBundle(
            "town.bunger.towns.plugin.i18n.messages",
            Locale.ENGLISH,
            TranslationManager.class.getClassLoader(),
            UTF8ResourceBundleControl.get()
        );
        REGISTRY.registerAll(Locale.ENGLISH, bundle, true);

        GlobalTranslator.translator().addSource(REGISTRY);
        registered = true;
    }
}
