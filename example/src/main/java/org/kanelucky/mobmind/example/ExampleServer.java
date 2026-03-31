package org.kanelucky.mobmind.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import org.kanelucky.mobmind.api.MobMind;
import org.kanelucky.mobmind.core.CoreInitializer;
import org.kanelucky.mobmind.vanilla.hostile.VanillaSkeleton;
import org.kanelucky.mobmind.vanilla.hostile.VanillaZombie;

public class ExampleServer {
    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();
        MobMind.INSTANCE.register(new CoreInitializer());
        MobMind.INSTANCE.init();

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Set the ChunkGenerator
        instanceContainer.setGenerator(
                unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setRespawnPoint(new Pos(0, 40, 0));
            player.setGameMode(GameMode.SURVIVAL);
            event.setSpawningInstance(instanceContainer);
        });
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            try {
                ExampleSheep sheep = new ExampleSheep();
                sheep.setInstance(instanceContainer, new Pos(0, 40, 0));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                VanillaZombie zombie = new VanillaZombie();
                zombie.setInstance(instanceContainer, new Pos(0, 40, 0));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                VanillaSkeleton skeleton = new VanillaSkeleton();
                skeleton.setInstance(instanceContainer, new Pos(10, 40, 10));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }
}
