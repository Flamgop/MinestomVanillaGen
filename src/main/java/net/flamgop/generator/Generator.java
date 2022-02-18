package net.flamgop.generator;

import net.flamgop.gui.MainWindow;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.flamgop.gui.MainWindow.*;

public class Generator implements ChunkGenerator {

    static int seed = 0;
    FastNoiseLite d2PerlinNoise;
    FastNoiseLite d3PerlinNoise;
    FastNoiseLite d3PerlinWarp;

    FastNoiseLite continentalnessNoise;
    FastNoiseLite erosionNoise;
    FastNoiseLite pvNoise;

    FastNoiseLite densityMap;
    FastNoiseLite caveDensityMap;

    FastNoiseLite caveNoise;
    FastNoiseLite caveNoiseWarp;

    FastNoiseLite caveNoise2;

    public Generator(int seed) {
        Generator.seed = seed;
        d2PerlinNoise = new FastNoiseLite();
        d2PerlinNoise.SetSeed(seed);
        d2PerlinNoise.SetFrequency(0.025f);
        d2PerlinNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        d2PerlinNoise.SetFractalOctaves(2);
        d2PerlinNoise.SetFractalType(FastNoiseLite.FractalType.FBm);

        d3PerlinNoise = new FastNoiseLite();
        d3PerlinNoise.SetSeed(seed);
        d3PerlinNoise.SetFrequency(0.025f);
        d3PerlinNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        d3PerlinNoise.SetFractalOctaves(2);
        d3PerlinNoise.SetFractalType(FastNoiseLite.FractalType.FBm);

        d3PerlinWarp = new FastNoiseLite();
        d3PerlinWarp.SetSeed(seed);
        d3PerlinWarp.SetFrequency(0.025f);
        d3PerlinWarp.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        d3PerlinWarp.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        d3PerlinWarp.SetDomainWarpAmp(0.5f);

        continentalnessNoise = new FastNoiseLite();
        continentalnessNoise.SetSeed(seed);
        continentalnessNoise.SetFrequency(0.008f);
        continentalnessNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);

        erosionNoise = new FastNoiseLite();
        erosionNoise.SetSeed(seed);
        erosionNoise.SetFrequency(0.0005f);
        erosionNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);

        pvNoise = new FastNoiseLite();
        pvNoise.SetSeed(seed);
        pvNoise.SetFrequency(0.06f);
        pvNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        pvNoise.SetFractalType(FastNoiseLite.FractalType.None);
        pvNoise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
        pvNoise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq);
        pvNoise.SetCellularJitter(1.0f);
        pvNoise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);

        densityMap = new FastNoiseLite();
        densityMap.SetSeed(seed);
        densityMap.SetFrequency(0.02f);
        densityMap.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);

        caveDensityMap = new FastNoiseLite();
        caveDensityMap.SetSeed(seed);
        caveDensityMap.SetFrequency(0.02f);
        caveDensityMap.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);

        caveNoise = new FastNoiseLite();
        caveNoise.SetSeed(seed);
        caveNoise.SetFrequency(0.4f);
        caveNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        caveNoise.SetFractalOctaves(1);
        caveNoise.SetFractalType(FastNoiseLite.FractalType.DomainWarpProgressive);

        caveNoiseWarp = new FastNoiseLite();
        caveNoiseWarp.SetSeed(seed);
        caveNoiseWarp.SetFrequency(0.02f);
        caveNoiseWarp.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        caveNoiseWarp.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
        caveNoiseWarp.SetDomainWarpAmp(0.5f);

        caveNoise2 = new FastNoiseLite();
        caveNoise2.SetSeed(seed);
        caveNoise2.SetFrequency(0.4f);
        caveNoise2.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        caveNoise2.SetFractalOctaves(1);
        caveNoise2.SetFractalType(FastNoiseLite.FractalType.DomainWarpProgressive);
    }

    public Generator() {
        this(new Random().nextInt());
    }

    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int globalX = chunkX * 16 + x;
                int globalZ = chunkZ * 16 + z;
                int minY = getHeight(globalX, globalZ) + 18;
                int minY_OLD = (getHeight_OLD(globalX, globalZ) + 18);
                float d2 = d2PerlinNoise.GetNoise(globalX, globalZ);
                float skew3d = 32;

                minY_OLD = (int) (minY_OLD * Math.abs(d2));
                for (int y = 0; y < 256; y++) {
//                    if (y < 53) batch.setBlock(x, y, z, Block.WATER);

                    if (y < minY_OLD) {
                        batch.setBlock(x, y, z, Block.STONE);
                    }


                    if (MainWindow.use3dNoise) {
                        minY_OLD -= 18;
                        FastNoiseLite.Vector3 vec3 = new FastNoiseLite.Vector3(globalX, y, globalZ);
                        d3PerlinWarp.DomainWarp(vec3);
                        float d3 = d3PerlinNoise.GetNoise(vec3.x, vec3.y, vec3.z);
                        float density = densityMap.GetNoise(vec3.x, vec3.y, vec3.z);
                        density /= 2;
                        float val = clamp(skew3d * density * clamp(d3 * 2, 1, -1) * 2.5f, skew3d, -skew3d);

                        if (y < Math.abs(val + minY_OLD)) batch.setBlock(x, y, z, Block.STONE);
                        minY_OLD += 18;
                    }
                }
            }
        }
    }

    private static double sigmoid(double x) {
        return x / (1 + Math.abs(x));
    }

    private static float clamp(float a, float max, float min) {
        return Math.max(Math.min(a, max), min);
    }

    // used for testing purposes, use the GUI (or replace them and use your own)
//    private static final double[] xDouble = new double[] {-1, -0.7, -0.5, -0.2, 0, 0.3, 0.35, 0.5, 0.65, 0.7, 1};
//    private static final double[] zDouble = new double[] {50, 52, 69, 72, 87, 99, 112, 134, 160, 189, 200};

    private static final AkimaSplineInterpolator asi = new AkimaSplineInterpolator();
    private static final PolynomialSplineFunction psfContinental = asi.interpolate(xSetContinental, ySetContinental);
    private static final PolynomialSplineFunction psfErosion = asi.interpolate(xSetErosion, ySetErosion);
    private static final PolynomialSplineFunction psfPV = asi.interpolate(xSetPV, ySetPV);

    public int getHeight(int x, int z) {
        float continentalness = continentalnessNoise.GetNoise(x, z);
        float erosion = erosionNoise.GetNoise(x, z);
        float pv = pvNoise.GetNoise(x, z);

        double psfContinentalVal = psfContinental.value(continentalness) + continentalness/2;
        // Make sure psfContinentalVal is between 40 and 230
        psfContinentalVal = Math.min(230, Math.max(40, psfContinentalVal));
        double psfErosionVal = psfErosion.value(psfContinentalVal) + erosion/2;
        // Make sure psfErosionVal is between -323 and 323
        psfErosionVal = Math.min(323, Math.max(-323, psfErosionVal));
        double psfPVVal = psfPV.value(psfErosionVal) + pv/2;
        // Make sure psfPVVal is between 50 and 200
        psfPVVal = Math.min(200, Math.max(50, psfPVVal));

        return Math.toIntExact(Math.round(psfPVVal));
    }

    /**
     * Deprecated because I *shouldn't* be using this.
     * Buuuut on the other hand it yields nicer looking results imo;
     *
     * @Deprecated Use getHeight instead
     */
    @Deprecated
    public int getHeight_OLD(int x, int z) {
        float continentalness = continentalnessNoise.GetNoise(x, z);
        return Math.toIntExact(Math.round(psfContinental.value(continentalness)));
    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        return List.of(
                new GrassNFoliagePopulator(),
                new TreePopulator(),
                new FlowerPatchPopulator(),
                new BiomePopulator(new Random().nextInt())
        );
    }

    private static class GrassNFoliagePopulator implements ChunkPopulator {

        private static final Random rng = new Random();

        @Override
        public void populateChunk(ChunkBatch batch, Chunk chunk) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 256; y > 0; y--) {
                        if (chunk.getBlock(x, y, z).isSolid() && chunk.getBlock(x, y + 1, z).isAir()) {
                            if (!chunk.getBlock(x, y - 1, z).isAir()) {
                                batch.setBlock(x, y, z, Block.GRASS_BLOCK);
                                for (int i = 0; i < 4; i++) {
                                    if (chunk.getBlock(x, y - i - 1, z).isSolid())
                                        batch.setBlock(x, y - i - 1, z, Block.DIRT);
                                }
                                if (rng.nextInt(10) == 0 && chunk.getBlock(x, y + 1, z).isAir()) {
                                    if (rng.nextInt(100) == 0) {
                                        batch.setBlock(x, y + 1, z, FlowerPatchPopulator.FLOWERS[rng.nextInt(FlowerPatchPopulator.FLOWERS.length)]);
                                    } else {
                                        batch.setBlock(x, y + 1, z, Block.GRASS);
                                    }
                                }
                            } else {
                                batch.setBlock(x, y, z, Block.AIR);
                            }
                        }
                    }
                }
            }
        }
    }

    private static class TreePopulator implements ChunkPopulator {

        Random rng = new Random();

        @Override
        public void populateChunk(ChunkBatch batch, Chunk chunk) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 256; y > 0; y--) {
                        if (rng.nextInt(100) != 0) continue;
                        if (x <= 1 || x >= 14 || z <= 1 || z >= 14) continue;

                        int height = 2 + rng.nextInt(3);
                        int radius = 5;

                        if (chunk.getBlock(x, y, z).isSolid() && !chunk.getBlock(x, y + 1, z).isSolid() && chunk.getBlock(x, y, z) != Block.OAK_LEAVES && chunk.getBlock(x, y+1, z) != Block.WATER) {
                            batch.setBlock(x, y, z, Block.DIRT);
                            for (int i = 0; i < height; i++) {
                                batch.setBlock(x, y + i, z, Block.OAK_LOG);
                            }
                            generateFoliage(batch, chunk, x, y + height+1, z, radius, 4);
                        }
                    }
                }
            }
        }

        public void generateFoliage(ChunkBatch batch, Chunk chunk, int x, int y, int z, int radius, int foliageHeight) {
            int newRadius = radius;
            for (int i = 0; i < foliageHeight; i++) {
                if (i % 2 == 0) {
                    newRadius /= 2;
                }
                generateSquare(batch, chunk, x, (y-1)+i, z, newRadius, Block.OAK_LEAVES, false);
            }
        }

        public void generateSquare(ChunkBatch batch, Chunk chunk, int x, int y, int z, int radius, Block block, boolean bigBoi) {
            int i = bigBoi ? 1 : 0;
            Pos pos;
            for (int j = -radius; j <= radius + i; ++j) {
                for (int k = -radius; k <= radius + i; ++k) {
                    if (this.isPositionInvalid(rng, j, y, k, radius, bigBoi)) continue;
                    pos = new Pos(x + j, y, z + k);
                    if (chunk.getBlock(pos).isAir()) batch.setBlock(pos, block);
                }
            }
        }

        public boolean isPositionInvalid(Random random, int dx, int y, int dz, int radius, boolean bigBoi) {
            int j;
            int i;
            if (bigBoi) {
                i = Math.min(Math.abs(dx), Math.abs(dx - 1));
                j = Math.min(Math.abs(dz), Math.abs(dz - 1));
            } else {
                i = Math.abs(dx);
                j = Math.abs(dz);
            }
            return this.isInvalidForLeaves(random, i, y, j, radius);
        }

        public boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius) {
            return dx == radius && dz == radius && random.nextInt(2) == 0;
        }
    }

    private static class BiomePopulator implements ChunkPopulator {

        //Biome Noise
        FastNoiseLite temperatureNoise;
        FastNoiseLite humidityNoise;

        private static final BiomeEffects C_DESERT_BIOME_EFFECTS = BiomeEffects.builder().grassColor(0x787129).foliageColor(0x787129).build();
        private static final CustomBiome C_DESERT = CustomBiome.builder()
                .name(NamespaceID.from("minecraft:desert"))
                .category(Biome.Category.DESERT)
                .temperature(0.75f)
                .downfall(0.25f)
                .precipitation(Biome.Precipitation.NONE)
                .effects(C_DESERT_BIOME_EFFECTS)
                .build();
        private static final Biome DESERT = C_DESERT.toBiome();

        private static final CustomBiome C_FOREST = CustomBiome.builder()
                .category(Biome.Category.FOREST)
                .downfall(0.5f)
                .precipitation(Biome.Precipitation.RAIN)
                .temperature(0.6f)
                .name(NamespaceID.from("minecraft:forest"))
                .build();
        private static final Biome FOREST = C_FOREST.toBiome();

        private static final BiomeEffects C_MOUNTAINS_BIOME_EFFECTS = BiomeEffects.builder().foliageColor(0x4f7853).grassColor(0x4f7853).build();
        private static final CustomBiome C_MOUNTAINS = CustomBiome.builder()
                .category(Biome.Category.EXTREME_HILLS)
                .downfall(0.5f)
                .precipitation(Biome.Precipitation.RAIN)
                .temperature(0.5f)
                .name(NamespaceID.from("minecraft:mountains"))
                .effects(C_MOUNTAINS_BIOME_EFFECTS)
                .build();
        private static final Biome MOUNTAINS = C_MOUNTAINS.toBiome();

        private static final CustomBiome C_SNOW = CustomBiome.builder()
                .category(Biome.Category.ICY)
                .downfall(0.5f)
                .precipitation(Biome.Precipitation.SNOW)
                .temperature(0.01f)
                .name(NamespaceID.from("minecraft:snow"))
                .effects(C_MOUNTAINS_BIOME_EFFECTS)
                .temperatureModifier(Biome.TemperatureModifier.FROZEN)
                .build();
        private static final Biome SNOW = C_SNOW.toBiome();

        private static final CustomBiome C_JUNGLE = CustomBiome.builder()
                .category(Biome.Category.JUNGLE)
                .downfall(0.89f)
                .precipitation(Biome.Precipitation.RAIN)
                .temperature(0.95f)
                .name(NamespaceID.from("minecraft:jungle"))
                .build();
        private static final Biome JUNGLE = C_JUNGLE.toBiome();

        public BiomePopulator(int seed) {
            //Biome Noise
            temperatureNoise = new FastNoiseLite();
            temperatureNoise.SetSeed(seed);
            temperatureNoise.SetFrequency(0.002f);
            temperatureNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
            temperatureNoise.SetFractalOctaves(4);
            temperatureNoise.SetFractalType(FastNoiseLite.FractalType.FBm);

            humidityNoise = new FastNoiseLite();
            humidityNoise.SetSeed(seed);
            humidityNoise.SetFrequency(0.002f);
            humidityNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
            humidityNoise.SetFractalOctaves(4);
            humidityNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        }
        
        public static final Map<CustomBiome, Biome> BIOMES = new HashMap<>();
        static {
            BIOMES.put(C_DESERT, DESERT);
            BIOMES.put(C_FOREST, FOREST);
            BIOMES.put(C_MOUNTAINS, MOUNTAINS);
            BIOMES.put(C_SNOW, SNOW);
            BIOMES.put(C_JUNGLE, JUNGLE);
        }

        @Override
        public void populateChunk(ChunkBatch batch, Chunk chunk) {
            // Each biome can be a minimum of 4x4x4 blocks
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 256; y++) {
                        float humidity = Math.abs(humidityNoise.GetNoise(x, y, z));
                        float temperature = Math.abs(temperatureNoise.GetNoise(x, y, z));

                        // Make sure humidity and temperature are in the range 0-1
                        humidity = Math.max(humidity, 0);
                        humidity = Math.min(humidity, 1);
                        temperature = Math.max(temperature, 0);
                        temperature = Math.min(temperature, 1);

                        Biome biome;
                        if (isDesert(temperature, humidity)) biome = DESERT;
                        else if (isForest(temperature, humidity)) biome = FOREST;
                        else if (isPlains(temperature, humidity)) biome = Biome.PLAINS;
                        else if (isMountain(temperature, humidity)) biome = MOUNTAINS;
                        else if (isSnow(temperature, humidity)) biome = SNOW;
                        else if (isJungle(temperature, humidity)) biome = JUNGLE;
                        else biome = FOREST;

                        chunk.setBiome(x, y, z, biome);
                    }
                }
            }
        }

        public boolean isHot(float temperature) {
            return temperature >= 0.75;
        }

        public boolean isCold(float temperature) {
            return temperature <= 0.25;
        }

        public boolean isWet(float humidity) {
            return humidity >= 0.75;
        }

        public boolean isDry(float humidity) {
            return humidity <= 0.25;
        }

        public boolean isWarm(float temperature) {
            return temperature >= 0.5 && temperature <= 0.75;
        }

        public boolean isCool(float temperature) {
            return temperature <= 0.5 && temperature >= 0.25;
        }

        public boolean isNormalHumidity(float humidity) {
            return humidity >= 0.25 && humidity <= 0.75;
        }

        public boolean isDesert(float temperature, float humidity) {
            return isHot(temperature) && isDry(humidity);
        }

        public boolean isForest(float temperature, float humidity) {
            return isWarm(temperature) && isNormalHumidity(humidity);
        }

        public boolean isPlains(float temperature, float humidity) {
            return isCool(temperature) && isNormalHumidity(humidity);
        }

        public boolean isMountain(float temperature, float humidity) {
            return isCool(temperature) && isWet(humidity);
        }

        public boolean isSnow(float temperature, float humidity) {
            return isCold(temperature) && isWet(humidity);
        }

        public boolean isJungle(float temperature, float humidity) {
            return isHot(temperature) && isWet(humidity);
        }
    }

    private static class FlowerPatchPopulator implements ChunkPopulator {

        private final Random rng = new Random();

        public static final Block[] FLOWERS = { Block.DANDELION, Block.POPPY, Block.BLUE_ORCHID, Block.ALLIUM, Block.AZURE_BLUET, Block.RED_TULIP, Block.ORANGE_TULIP, Block.WHITE_TULIP, Block.PINK_TULIP, Block.OXEYE_DAISY };

        @Override
        public void populateChunk(ChunkBatch batch, Chunk chunk) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 256; y++) {
                        if (x <= 1 || x >= 14 || z <= 1 || z >= 14) continue;
                        if (rng.nextInt(1000) == 0 && chunk.getBlock(x, y, z) != Block.WATER) {
                            final Block flower = FLOWERS[rng.nextInt(FLOWERS.length)];

                            int circleRadius = rng.nextInt(3) + 4;
                            for (int i = -circleRadius; i <= circleRadius; i++) {
                                for (int j = -circleRadius; j <= circleRadius; j++) {
                                    for (int k = -circleRadius; k <= circleRadius; k++) {
                                        if (i * i + j * j + k * k <= circleRadius * circleRadius) {
                                            if (chunk.getBlock(x + i, y + j - 1, z + k) == Block.GRASS_BLOCK &&
                                                    chunk.getBlock(x + i, y + j, z + k) == Block.AIR &&
                                                    rng.nextInt(5) == 0) {
                                                batch.setBlock(x + i, y + j, z + k, flower);
                                            }
                                        }
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
