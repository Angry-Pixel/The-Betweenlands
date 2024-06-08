package thebetweenlands.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BetweenlandsDebugBlock extends BetweenlandsBlock{

	public BetweenlandsDebugBlock(Properties p_48756_) {
		super(p_48756_);
	}
	
	
	public void animateTick(BlockState p_55479_, Level p_55480_, BlockPos p_55481_, Random p_55482_) {
		// Constantly displayes runtime vars for assesment
		
	}
}
