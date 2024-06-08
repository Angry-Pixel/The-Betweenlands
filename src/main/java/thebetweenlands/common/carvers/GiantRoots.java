package thebetweenlands.common.carvers;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.gen.IBlockStateAccessOnly;
import thebetweenlands.common.world.gen.biome.feature.CoarseIslandsFeature;
import thebetweenlands.common.world.gen.feature.legacy.WorldGenGiantRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

// TODO: convert to feature as it behaves like one
public class GiantRoots extends WorldCarver<CarverConfiguration> {
    // Unused
    protected CoarseIslandsFeature coarseIslandsFeature = new CoarseIslandsFeature();

    protected List<WorldGenGiantRoot> giantRootGens = new ArrayList<>();

    // TODO: change to holder biomes
    //    + make datapack compatible
    private static final Set<Holder<Biome>> noBreakBiomes
            = ImmutableSet.of(
            BiomeRegistry.COARSE_ISLANDS.biome.getHolder().get(),
            BiomeRegistry.RAISED_ISLES.biome.getHolder().get());

    public GiantRoots(Codec p_159366_) {
        super(p_159366_);
    }

    @Override
    public boolean carve(CarvingContext p_190766_, CarverConfiguration p_190767_, ChunkAccess primer, Function<BlockPos, Holder<Biome>> p_190769_, Random p_190770_, Aquifer p_190771_, ChunkPos p_190772_, CarvingMask p_190773_) {
        this.giantRootGens.clear();
        int x = primer.getPos().x;
        int z = primer.getPos().z;

        for(WorldGenGiantRoot root : this.giantRootGens) {
            root.setGenBounds(new AABB(x * 16, 0, z * 16, x * 16 + 15, 256, z * 16 + 15));
            Random rootRand = new Random();
            rootRand.setSeed(root.start.getX() ^ root.start.getY() ^ root.start.getZ());// ^ p_190767_.getSeed());
            root.generate(IBlockStateAccessOnly.from(x, z, primer), x, z, true, rootRand, root.start);
        }

        return true;
    }

    @Override
    public boolean isStartChunk(CarverConfiguration p_159384_, Random p_159385_) {
        return p_159385_.nextBoolean();
    }
}
