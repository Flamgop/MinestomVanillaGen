package net.flamgop;

import net.flamgop.generator.Generator;
import net.flamgop.gui.MainWindow;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.lan.OpenToLAN;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.world.DimensionType;

public class Main {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        DimensionType type = DimensionType.builder(NamespaceID.from("test:test")).ambientLight(2f).build();
        MinecraftServer.getDimensionTypeManager().addDimension(type);

        MainWindow window = new MainWindow();

        InstanceContainer container = MinecraftServer.getInstanceManager().createInstanceContainer(type);
        container.setChunkGenerator(new Generator());
        PlayerInit(container);
        MinecraftServer.getCommandManager().register(new GetSplineSetsCommand());
        MinecraftServer.getCommandManager().register(new GameModeCommand());

        MinecraftServer.setChunkViewDistance(16);

        OpenToLAN.open();
        server.start("0.0.0.0", 25565);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                container.loadChunk(i, j);
            }
        }
    }

    public static void PlayerInit(InstanceContainer container) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, (event) -> {
            event.setSpawningInstance(container);
            container.loadChunk(0, 0).whenComplete((chunk, throwable) -> {
                for (int y = 256; y > 0; y--) {
                    if (chunk.getBlock(0, y, 0).isSolid()) {
                        event.getPlayer().setRespawnPoint(new Pos(0, y + 1, 0));
                        event.getPlayer().setGameMode(GameMode.CREATIVE);
                        int finalY = y;
                        MinecraftServer.getSchedulerManager().buildTask(() -> {
                            event.getPlayer().teleport(new Pos(0, finalY + 1, 0));
                            event.getPlayer().setGameMode(GameMode.SPECTATOR);
                        }).delay(10000, TimeUnit.MILLISECOND).schedule();
                        break;
                    }
                }
            });
        });
    }
}
