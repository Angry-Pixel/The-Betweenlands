package thebetweenlands.common.world.gen.feature;

import java.util.Map.Entry;
import java.util.Random;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBlockReplacementCluster extends WorldGenerator {
	private final IBlockState blockState;
	private final int offset;
	private final int attempts;
	private Predicate<IBlockState> matcher;
	private boolean inheritProperties;

	public WorldGenBlockReplacementCluster(IBlockState blockState, int offset, int attempts, Predicate<IBlockState> matcher) {
		this.blockState = blockState;
		this.offset = offset;
		this.attempts = attempts;
		this.matcher = matcher;
	}

	public WorldGenBlockReplacementCluster(IBlockState blockState, Predicate<IBlockState> matcher) {
		this(blockState, 8, 128, matcher);
	}

	public WorldGenBlockReplacementCluster setInheritProperties(boolean inherit) {
		this.inheritProperties = inherit;
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		boolean generated = false;

		for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
			position = position.down();
		}

		for (int i = 0; i < this.attempts; ++i) {
			BlockPos blockpos = position.add(rand.nextInt(this.offset) - rand.nextInt(this.offset), rand.nextInt(this.offset / 2 + 1) - rand.nextInt(this.offset / 2 + 1), rand.nextInt(this.offset) - rand.nextInt(this.offset));

			if (worldIn.isBlockLoaded(blockpos)) {
				IBlockState state = worldIn.getBlockState(blockpos);
				if (this.matcher.apply(state)) {
					IBlockState setState = this.blockState;
					if (this.inheritProperties) {
						ImmutableMap<IProperty<?>, Comparable<?>> properties = state.getProperties();
						for (Entry<IProperty<?>, Comparable<?>> property : properties.entrySet()) {
							IProperty sourceProperty = property.getKey();
							IProperty targetProperty = setState.getBlock().getBlockState().getProperty(sourceProperty.getName());
							if (targetProperty != null && sourceProperty.getValueClass() == targetProperty.getValueClass()) {
								setState = setState.withProperty(targetProperty, (Comparable) property.getValue());
							}
						}
					}
					this.setBlockAndNotifyAdequately(worldIn, blockpos, setState);
					generated = true;
				}
			}
		}

		return generated;
	}
}