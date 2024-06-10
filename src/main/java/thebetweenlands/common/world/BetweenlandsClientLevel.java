package thebetweenlands.common.world;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.dimension.BetweenlandsClientChunkCache;

@OnlyIn(Dist.CLIENT)
public class BetweenlandsClientLevel extends ClientLevel {

	public BetweenlandsClientLevel(ClientPacketListener p_205505_, ClientLevelData p_205506_,
			ResourceKey<Level> p_205507_, Holder<DimensionType> p_205508_, int p_205509_, int p_205510_,
			Supplier<ProfilerFiller> p_205511_, LevelRenderer p_205512_, boolean p_205513_, long p_205514_) {
		super(p_205505_, p_205506_, p_205507_, p_205508_, p_205509_, p_205510_, p_205511_, p_205512_, p_205513_, p_205514_);
		this.chunkSource = new BetweenlandsClientChunkCache(this, p_205509_);
	}

	public int getBrightness(LightLayer p_45518_, BlockPos p_45519_) {
		return this.getLightEngine().getLayerListener(p_45518_).getLightValue(p_45519_);
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
