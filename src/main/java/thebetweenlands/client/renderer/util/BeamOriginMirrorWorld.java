package thebetweenlands.client.renderer.util;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import thebetweenlands.common.block.structure.BrazierBlock;
import thebetweenlands.common.registries.BlockRegistry;

// Necessary if we want to use BlockRenderDispatcher::renderBatched
public class BeamOriginMirrorWorld implements BlockAndTintGetter, LevelHeightAccessor, LightChunkGetter {

	// Even though I really wanted to have it line up with the real world, this wouldn't allow batched rendering
	protected float ambientOcclusionLightValue = 0.9f;
	protected int modelLightValue = 255;

	protected final MirrorWorldLightEngine lightEngine = new MirrorWorldLightEngine(this);

	public float getAOLightValue() {
		return this.ambientOcclusionLightValue;
	}

	public void setAOLightValue(float value) {
		if(Float.isNaN(value)) {
			throw new IllegalArgumentException("'value' cannot be NaN");
		}
		this.ambientOcclusionLightValue = Math.clamp(value, 0.0f, 1.0f);
	}

	public int getModelLightValue() {
		return this.modelLightValue;
	}

	public void setModelLightValue(int value) {
		this.modelLightValue = value & 0xFFFF;
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	// if beam origin is 0, 4, 0 then:
	// brazier bottom 	at ~3, 1, ~3
	// brazier top 		at ~3, 2, ~3
	// fire 			at ~3, 3, ~3
	public BlockState getBlockState(BlockPos pos) {
		if((pos.getX() == 3 || pos.getX() == -3) && (pos.getZ() == 3 || pos.getZ() == -3)) {
			if(pos.getY() == 1) {
				return BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.LOWER);
			} else if(pos.getY() == 2) {
				return BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.UPPER);
			} else if(pos.getY() == 3) {
				return Blocks.FIRE.defaultBlockState();
			}
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public int getMinBuildHeight() {
		return 0;
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return this.ambientOcclusionLightValue;
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return this.lightEngine;
	}

	@Override
	@Nullable
	public LightChunk getChunkForLighting(int chunkX, int chunkZ) {
		return null;
	}

	@Override
	public BlockGetter getLevel() {
		return this;
	}

	@Override
	public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
		return 0xFFFFFF;
	}

	@Override
	public int getBrightness(LightLayer lightType, BlockPos blockPos) {
		return switch (lightType) {
			case BLOCK -> (this.getModelLightValue() & 0xFF) >> 4;
			case SKY -> ((this.getModelLightValue() >> 8) & 0xFF) >> 4;
		};
	}

	public static class MirrorWorldLightEngine extends LevelLightEngine {

		protected final BeamOriginMirrorWorld mirrorWorld;

		public MirrorWorldLightEngine(BeamOriginMirrorWorld mirrorWorld) {
			super(mirrorWorld, false, false);
			this.mirrorWorld = mirrorWorld;
		}

		@Override
		public int getRawBrightness(BlockPos blockPos, int amount) {
			int brightness = this.mirrorWorld.getModelLightValue();
			int skyLight = (((brightness >> 8) & 0xFF) >> 4) - amount;
			int blockLight = (brightness & 0xFF) >> 4;
			return Math.max(skyLight, blockLight);
		}
	}
}
