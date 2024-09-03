package thebetweenlands.common.block.waterlog;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

public interface SwampWaterLoggable extends BucketPickup, LiquidBlockContainer {

	EnumProperty<WaterType> WATER_TYPE = EnumProperty.create("water_type", WaterType.class);

	@Override
	default boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return state.hasProperty(WATER_TYPE) && state.getValue(WATER_TYPE) == WaterType.NONE && Ref.FLUIDS.containsKey(fluid);
	}

	@Override
	default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		Fluid stateFluid = state.getValue(WATER_TYPE).fluid;

		if (stateFluid != fluidState.getType() && Ref.FLUIDS.containsKey(fluidState.getType())) {
			if (!level.isClientSide()) {
				level.setBlock(pos, state.setValue(WATER_TYPE, Ref.FLUIDS.get(fluidState.getType())), 3);
				level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
			}
			return true;
		}
		return false;
	}

	@Override
	default ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
		Fluid stateFluid = state.getValue(WATER_TYPE).fluid;

		if (stateFluid != Fluids.EMPTY) {
			level.setBlock(pos, state.setValue(WATER_TYPE, WaterType.NONE), 3);
		}

		return new ItemStack(stateFluid.getBucket());
	}

	@Override
	default Optional<SoundEvent> getPickupSound() {
		return Optional.empty();
	}

	@Override
	default Optional<SoundEvent> getPickupSound(BlockState state) {
		return state.getValue(WATER_TYPE).fluid.getPickupSound();
	}

	enum WaterType implements StringRepresentable {
		NONE(Blocks.AIR, Fluids.EMPTY),
		WATER(Blocks.WATER, Fluids.WATER),
		SWAMP_WATER(BlockRegistry.SWAMP_WATER.get(), FluidRegistry.SWAMP_WATER_STILL.get());

		private final Block block;
		private final Fluid fluid;
		private final String name;

		WaterType(Block block, Fluid fluid) {
			this.block = block;
			this.fluid = fluid;
			this.name = name().toLowerCase(Locale.ROOT);

			Ref.FLUIDS.put(fluid, this);
		}

		public static WaterType getFromFluid(Fluid fluid) {
			return Ref.FLUIDS.getOrDefault(fluid, NONE);
		}

		@Override
		public String getSerializedName() {
			return name;
		}

		public Fluid getFluid() {
			return fluid;
		}

		public Block getBlock() {
			return block;
		}

	}

	class Ref {
		private final static HashMap<Fluid, WaterType> FLUIDS = new HashMap<>();
	}
}
