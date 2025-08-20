package thebetweenlands.common.fluid;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import thebetweenlands.common.block.terrain.TarBlock;

/*
 * WaterFluid extends FlowingFluid, so this should be fine
 * Originally I was worried that extending BaseFlowingFluid.Source and
 * BaseFlowingFluid.Flowing would be necessary for compatibility,
 * but I've since figured out that this isn't the case
 */
public abstract class TarFlowingFluid extends BaseFlowingFluid {
	/*
	 * All the boiling stuff was just me seeing what the new fluid system
	 * was capable of, so feel free to remove it
	 */
	public static final BooleanProperty BOILING = BooleanProperty.create("boiling");
	
	public TarFlowingFluid(Properties properties) {
		super(properties);
		registerDefaultState(defaultFluidState().setValue(BOILING, false));
	}

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        builder.add(BOILING);
    }
    
    @Override
    protected BlockState createLegacyBlock(FluidState state) {
    	// trySetValue to avoid errors if it returns the AIR default
    	return super.createLegacyBlock(state).trySetValue(BOILING, state.getValue(BOILING));
    }
    
    @Override
    protected FluidState getNewLiquid(Level level, BlockPos pos, BlockState blockState) {
    	FluidState state = super.getNewLiquid(level, pos, blockState);
    	if(!state.isEmpty()) {
        	state = state.trySetValue(BOILING, TarBlock.shouldBeBoiling(level, pos));
    	}
    	return state;
    }
    
    // Fix: buckets will always place with the default state even when they should be boiling (ignoring the FluidType getStateForPlacement method)
    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
    	BlockState blockState = level.getBlockState(pos);
    	if(state.isSource() && Objects.equals(blockState, state.createLegacyBlock())) {
    		var isBoiling = state.getOptionalValue(BOILING);
    		if(isBoiling.isPresent()) {
        		boolean shouldBeBoiling = TarBlock.shouldBeBoiling(level, pos);
        		if(state.getValue(BOILING) != shouldBeBoiling) {
                    FluidState fluidstate = state.trySetValue(BOILING, shouldBeBoiling);
                    int i = this.getSpreadDelay(level, pos, state, fluidstate);
                    
                    state = fluidstate;
                    BlockState blockstate = fluidstate.createLegacyBlock();
                    level.setBlock(pos, blockstate, 2);
                    level.scheduleTick(pos, fluidstate.getType(), i);
                    level.updateNeighborsAt(pos, blockstate.getBlock());
        		}
    		}
    	}
    	
    	super.tick(level, pos, state);
    }
    
	public static class Flowing extends TarFlowingFluid {
        public Flowing(Properties properties) {
            super(properties);
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

	public static class Source extends TarFlowingFluid {
		public Source(Properties properties) {
			super(properties);
		}

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
	}
}
