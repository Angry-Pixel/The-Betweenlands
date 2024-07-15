package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

public class SpreadingSludgyDirtBlock extends SpreadingDeathBlock {

	protected static final VoxelShape DIRT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

	public SpreadingSludgyDirtBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getMaxLocalRawBrightness(pos.above()) < 4 && level.getBrightness(LightLayer.BLOCK, pos.above()) > 2) {
			level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState());
			this.checkAndRevertBiome(level, pos);
		} else {
			super.tick(state, level, pos, random);
		}
	}

	@Override
	public boolean canSpreadInto(Level level, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		return super.canSpreadInto(level, pos, state, offsetPos, offsetState) && offsetState.is(BlockTags.DIRT);
	}

	@Override
	public void spreadInto(Level level, BlockPos pos, BlockState state, BlockPos offsetPos, BlockState offsetState) {
		level.setBlockAndUpdate(offsetPos, this.defaultBlockState());
		for (int yo = 1; yo < 6; yo++) {
			if (this.canSpreadInto(level, pos, state, offsetPos.below(yo), level.getBlockState(offsetPos.below(yo)))) {
				level.setBlockAndUpdate(offsetPos.below(yo), BlockRegistry.MUD.get().defaultBlockState());
			}
		}
		if (level.getRandom().nextInt(3) == 0 && level.isEmptyBlock(offsetPos.above())) {
			level.setBlockAndUpdate(offsetPos.above(), BlockRegistry.DEAD_WEEDWOOD_BUSH.get().defaultBlockState());
		}
	}

	@Override
	protected boolean shouldSpread(Level level, BlockPos pos, BlockState state) {
		return level.getRandom().nextBoolean();
	}

	@Nullable
	@Override
	public ResourceKey<Biome> getSpreadingBiome() {
		return BiomeRegistry.SLUDGE_PLAINS;
	}

	@Nullable
	@Override
	public ResourceKey<Biome> getPreviousBiome() {
		return BiomeRegistry.SWAMPLANDS_CLEARING;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return DIRT_AABB;
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		entity.makeStuckInBlock(state, new Vec3(0.25D, 0.05D, 0.25D));
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return new ItemStack(BlockRegistry.SWAMP_DIRT);
	}
}
