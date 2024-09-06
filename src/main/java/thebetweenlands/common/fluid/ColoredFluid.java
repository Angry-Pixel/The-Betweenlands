package thebetweenlands.common.fluid;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class ColoredFluid extends BaseFlowingFluid {

	private final int color;

	public ColoredFluid(int color, Properties properties) {
		super(properties);
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	public static class Flowing extends ColoredFluid {
		public Flowing(int color, Properties properties) {
			super(color, properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends ColoredFluid {
		public Source(int color, Properties properties) {
			super(color, properties);
		}

		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}
}
