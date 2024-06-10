package thebetweenlands.common.world;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands.BetweenlandsGeneratorSettings;

public class BetweenlandsLevel extends Level {
	
	public BetweenlandsLevel(WritableLevelData p_204149_, ResourceKey<Level> p_204150_,
			Holder<DimensionType> p_204151_, Supplier<ProfilerFiller> p_204152_, boolean p_204153_, boolean p_204154_,
			long p_204155_) {
		super(p_204149_, p_204150_, p_204151_, p_204152_, p_204153_, p_204154_, p_204155_);
	}

	@Override
	public LevelTickAccess<Block> getBlockTicks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LevelTickAccess<Fluid> getFluidTicks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkSource getChunkSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void levelEvent(Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEvent(Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RegistryAccess registryAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Player> players() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Holder<Biome> getUncachedNoiseBiome(int p_204159_, int p_204160_, int p_204161_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getShade(Direction p_45522_, boolean p_45523_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playSound(Player p_46543_, double p_46544_, double p_46545_, double p_46546_, SoundEvent p_46547_,
			SoundSource p_46548_, float p_46549_, float p_46550_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playSound(Player p_46551_, Entity p_46552_, SoundEvent p_46553_, SoundSource p_46554_, float p_46555_,
			float p_46556_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String gatherChunkSourceStats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getEntity(int p_46492_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapItemSavedData getMapData(String p_46650_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMapData(String p_151533_, MapItemSavedData p_151534_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFreeMapId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void destroyBlockProgress(int p_46506_, BlockPos p_46507_, int p_46508_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scoreboard getScoreboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecipeManager getRecipeManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected LevelEntityGetter<Entity> getEntities() {
		// TODO Auto-generated method stub
		return null;
	}
}
