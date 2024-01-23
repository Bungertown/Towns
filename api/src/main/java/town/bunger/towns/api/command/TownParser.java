package town.bunger.towns.api.command;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.parser.ParserDescriptor;
import cloud.commandframework.arguments.suggestion.BlockingSuggestionProvider;
import cloud.commandframework.arguments.suggestion.Suggestion;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.context.CommandInput;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.town.Town;

public final class TownParser<C> implements ArgumentParser<C, Town>, BlockingSuggestionProvider<C> {

    public static <C> ParserDescriptor<C, Town> of() {
        return ParserDescriptor.of(new TownParser<>(null), Town.class);
    }

    public static <C> ParserDescriptor<C, Town> of(String prefix) {
        return ParserDescriptor.of(new TownParser<>(prefix), Town.class);
    }

    private final @Nullable String prefix;

    private TownParser(@Nullable String prefix) {
        this.prefix = prefix;
    }


    @Override
    public ArgumentParseResult<Town> parse(CommandContext<C> context, CommandInput commandInput) {
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();

        String input = commandInput.peekString();
        if (input.isEmpty()) {
            return ArgumentParseResult.failure(new TownParseException(input, context));
        }
        if (this.prefix != null) {
            if (!input.startsWith(this.prefix)) {
                return ArgumentParseResult.failure(new IllegalArgumentException("Must specify prefix '" + this.prefix + "' before town name"));
            }
            input = input.substring(this.prefix.length());
        }

        final Town town = api.towns().get(input);
        if (town != null) {
            commandInput.readString();
            return ArgumentParseResult.success(town);
        } else {
            return ArgumentParseResult.failure(new IllegalArgumentException("Town not found"));
        }
    }

    @Override
    public Iterable<Suggestion> suggestions(CommandContext<C> context, CommandInput input) {
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();

        return api.towns().allNames().stream()
            .map(name -> {
                if (this.prefix == null) {
                    return Suggestion.simple(name);
                } else {
                    return Suggestion.simple(this.prefix + name);
                }
            })
            .toList();
    }


    public static final class TownParseException extends ParserException {

        public TownParseException(final String input, final CommandContext<?> context) {
            super(TownParser.class, context, Caption.of("bunger.towns.parser.town.not-found"), CaptionVariable.of("input", input));
        }
    }
}
