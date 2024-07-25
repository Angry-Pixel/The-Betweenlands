package thebetweenlands.common.block;

import com.google.common.base.Predicates;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class RootBlock extends Block {

	public static final ModelProperty<Boolean> NO_BOTTOM = new ModelProperty<Boolean>(Predicates.<Boolean>notNull());
	public static final ModelProperty<Boolean> NO_TOP = new ModelProperty<Boolean>(Predicates.<Boolean>notNull());
	public static final ModelProperty<Integer> DIST_UP = new ModelProperty<Integer>(Predicates.<Integer>notNull());
	public static final ModelProperty<Integer> DIST_DOWN = new ModelProperty<Integer>(Predicates.<Integer>notNull());
	public static final ModelProperty<Integer> POS_X = new ModelProperty<Integer>(Predicates.<Integer>notNull());
	public static final ModelProperty<Integer> POS_Y = new ModelProperty<Integer>(Predicates.<Integer>notNull());
	public static final ModelProperty<Integer> POS_Z = new ModelProperty<Integer>(Predicates.<Integer>notNull());
	
	public RootBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
//		builder.add(null)
	}
	
	
}
