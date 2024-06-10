package thebetweenlands.common.world;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.dimension.BetweenlandsClientChunkCache;
import thebetweenlands.common.dimension.BetweenlandsServerChunkCache;

public class BetweenlandsServerLevel extends ServerLevel {

	public BetweenlandsServerLevel(MinecraftServer p_203762_, Executor p_203763_, LevelStorageAccess p_203764_,
			ServerLevelData p_203765_, ResourceKey<Level> p_203766_, Holder<DimensionType> p_203767_,
			ChunkProgressListener p_203768_, ChunkGenerator p_203769_, boolean p_203770_, long p_203771_,
			List<CustomSpawner> p_203772_, boolean p_203773_) {
		super(p_203762_, p_203763_, p_203764_, p_203765_, p_203766_, p_203767_, p_203768_, p_203769_, p_203770_, p_203771_,
				p_203772_, p_203773_);
	}

	//  f_46425_
	public void updateSkyBrightness() {
		double d0 = 1.0D - (double)(this.getRainLevel(1.0F) * 5.0F) / 16.0D;
		double d1 = 1.0D - (double)(this.getThunderLevel(1.0F) * 5.0F) / 16.0D;
		double d2 = 0.5D + 2.0D * Mth.clamp((double)Mth.cos(this.getTimeOfDay(1.0F) * ((float)Math.PI * 2F)), -0.25D, 0.25D);
		//this.skyDarken = (int)((1.0D - d2 * d0 * d1) * 11.0D);

		this.skyDarken = 10;
	}
}
