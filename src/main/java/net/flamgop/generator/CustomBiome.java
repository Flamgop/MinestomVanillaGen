package net.flamgop.generator;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;

import java.util.function.BiConsumer;

public class CustomBiome {

    /*
    *
    * This class is used to add your own variables to biomes
    * I intended for this to be used for possibly different spline sets or different intensities of noise for terrain.
    * not sure what else it could be used for
    */

    private static final BiomeEffects DEFAULT_EFFECTS = BiomeEffects.builder()
            .fogColor(0xC0D8FF)
            .skyColor(0x78A7FF)
            .waterColor(0x3F76E4)
            .waterFogColor(0x50533)
            .build();

    @Getter private final NamespaceID name;
    @Getter private final float depth;
    @Getter private final float temperature;
    @Getter private final float scale;
    @Getter private final float downfall;
    @Getter private final Biome.Category category;
    @Getter private final BiomeEffects effects;
    @Getter private final Biome.Precipitation precipitation;
    @Getter private final Biome.TemperatureModifier temperatureModifier;
    // This is custom, I'm not sure a better way to implement it lol
    @Getter private final BiConsumer<ChunkBatch, Chunk> biomePopulator;
    private final Biome biome;

    public CustomBiome(NamespaceID name, float depth, float temperature, float scale, float downfall, Biome.Category category, BiomeEffects effects, Biome.Precipitation precipitation, Biome.TemperatureModifier temperatureModifier, BiConsumer<ChunkBatch, Chunk> biomePopulator) {
        this.category = category;
        this.depth = depth;
        this.downfall = downfall;
        this.effects = effects;
        this.name = name;
        this.precipitation = precipitation;
        this.scale = scale;
        this.temperature = temperature;
        this.temperatureModifier = temperatureModifier;
        this.biomePopulator = biomePopulator;
        this.biome = Biome.builder().name(name).category(category).depth(depth).effects(effects).precipitation(precipitation).temperature(temperature).temperatureModifier(temperatureModifier).scale(scale).downfall(downfall).build();
        MinecraftServer.getBiomeManager().addBiome(biome);
    }

    public CustomBiome(Biome biome, BiConsumer<ChunkBatch, Chunk> biomePopulator) {
        this(biome.name(), biome.depth(), biome.temperature(), biome.scale(), biome.downfall(), biome.category(), biome.effects(), biome.precipitation(), biome.temperatureModifier(), biomePopulator);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void apply(ChunkBatch batch, Chunk chunk) {
        biomePopulator.accept(batch, chunk);
    }

    public Biome toBiome() {
        return biome;
    }

    public static final class Builder {
        private NamespaceID name;
        private float depth = 0.2f;
        private float temperature = 0.25f;
        private float scale = 0.2f;
        private float downfall = 0.8f;
        private Biome.Category category = Biome.Category.NONE;
        private BiomeEffects effects = DEFAULT_EFFECTS;
        private Biome.Precipitation precipitation = Biome.Precipitation.RAIN;
        private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
        private BiConsumer<ChunkBatch, Chunk> biomePopulator;

        Builder() {
        }

        public Builder name(NamespaceID name) {
            this.name = name;
            return this;
        }

        public Builder depth(float depth) {
            this.depth = depth;
            return this;
        }

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder downfall(float downfall) {
            this.downfall = downfall;
            return this;
        }

        public Builder category(Biome.Category category) {
            this.category = category;
            return this;
        }

        public Builder effects(BiomeEffects effects) {
            this.effects = effects;
            return this;
        }

        public Builder precipitation(Biome.Precipitation precipitation) {
            this.precipitation = precipitation;
            return this;
        }

        public Builder temperatureModifier(Biome.TemperatureModifier temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return this;
        }

        public Builder biomePopulator(BiConsumer<ChunkBatch, Chunk> biomePopulator) {
            this.biomePopulator = biomePopulator;
            return this;
        }

        public CustomBiome build() {
            return new CustomBiome(name, depth, temperature, scale, downfall, category, effects, precipitation, temperatureModifier, biomePopulator);
        }
    }
}
