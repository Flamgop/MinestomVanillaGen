package net.flamgop;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class GameModeCommand extends Command {
    public GameModeCommand() {
        super("gamemode");

        Argument<GameMode> gmArg = ArgumentType.Enum("gamemode", GameMode.class);

        addSyntax(((sender, context) -> {
            GameMode gm = context.get(gmArg);
            Player player = (Player) sender;
            player.setGameMode(gm);
        }), gmArg);
    }
}
