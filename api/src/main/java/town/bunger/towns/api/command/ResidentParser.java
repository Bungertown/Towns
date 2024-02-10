package town.bunger.towns.api.command;

import net.kyori.adventure.util.TriState;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ResidentParser<C> implements ArgumentParser.FutureArgumentParser<C, Resident>, BlockingSuggestionProvider<C> {

    public static <C> ParserDescriptor<C, Resident> anyTown() {
        return ParserDescriptor.of(new ResidentParser<>(TriState.NOT_SET), Resident.class);
    }

    public static <C> ParserDescriptor<C, Resident> sameTown() {
        return ParserDescriptor.of(new ResidentParser<>(TriState.TRUE), Resident.class);
    }

    public static <C> ParserDescriptor<C, Resident> notSameTown() {
        return ParserDescriptor.of(new ResidentParser<>(TriState.FALSE), Resident.class);
    }

    private final TriState sameTown;

    private ResidentParser(TriState sameTown) {
        this.sameTown = sameTown;
    }

    @Override
    public CompletableFuture<ArgumentParseResult<Resident>> parseFuture(
        CommandContext<C> context,
        CommandInput commandInput
    ) {
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();

        String input = commandInput.peekString();
        if (input.isEmpty()) {
            return CompletableFuture.completedFuture(
                ArgumentParseResult.failure(new ResidentParseException(input, context))
            );
        }

        if (this.sameTown != TriState.NOT_SET) {
            final UUID residentId = api.residents().getUUID(input);
            if (residentId == null) {
                return CompletableFuture.completedFuture(
                    ArgumentParseResult.failure(new IllegalArgumentException("Resident not found"))
                );
            }

            final Town town = context.inject(Town.class).orElse(null);
            if (town == null) {
                return CompletableFuture.completedFuture(
                    ArgumentParseResult.failure(new IllegalArgumentException("You are not a member of a town"))
                );
            }

            switch (this.sameTown) {
                case TRUE -> {
                    if (!town.hasResident(residentId)) {
                        return CompletableFuture.completedFuture(
                            ArgumentParseResult.failure(new IllegalArgumentException("Resident is not a member of your town"))
                        );
                    }
                }
                case FALSE -> {
                    if (town.hasResident(residentId)) {
                        return CompletableFuture.completedFuture(
                            ArgumentParseResult.failure(new IllegalArgumentException("Resident is a member of your town"))
                        );
                    }
                }
            }
        }

        return api.residents()
            .loadPlayer(input)
            .thenApply(resident -> {
                if (resident != null) {
                    commandInput.readString();
                    return ArgumentParseResult.success(resident);
                } else {
                    return ArgumentParseResult.failure(new IllegalArgumentException("Resident not found"));
                }
            });
    }

    @Override
    public Iterable<Suggestion> suggestions(CommandContext<C> context, CommandInput input) {
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();

        switch (this.sameTown) {
            case FALSE -> {
                final Town town = context.inject(Town.class).orElse(null);
                if (town == null) {
                    return List.of();
                }
                final var names = new HashSet<>(api.residents().allNames());
                names.removeAll(town.residentNames());
                return names.stream()
                    .map(Suggestion::simple)
                    .toList();
            }
            case TRUE -> {
                final Town town = context.inject(Town.class).orElse(null);
                if (town == null) {
                    return List.of();
                }
                return town.residentNames().stream()
                    .map(Suggestion::simple)
                    .toList();
            }
        }

        return api.residents().allNames().stream()
            .map(Suggestion::simple)
            .toList();

    }


    public static final class ResidentParseException extends ParserException {

        public ResidentParseException(final String input, final CommandContext<?> context) {
            super(ResidentParser.class, context, Caption.of("bunger.towns.parser.resident.not-found"), CaptionVariable.of("input", input));
        }
    }
}
