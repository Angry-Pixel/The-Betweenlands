package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.SulfurFurnaceBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class SulfurFurnaceBlock extends AbstractFurnaceBlock {

	public SulfurFurnaceBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends AbstractFurnaceBlock> codec() {
		return null;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT)) {
			double d0 = pos.getX() + 0.5D;
			double d1 = pos.getY();
			double d2 = pos.getZ() + 0.5D;
			if (random.nextDouble() < 0.1D) {
				level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}

			Direction direction = state.getValue(FACING);
			Direction.Axis axis = direction.getAxis();
			double d4 = random.nextDouble() * 0.6D - 0.3D;
			double d5 = axis == Direction.Axis.X ? direction.getStepX() * 0.52D : d4;
			double d6 = random.nextDouble() * 9.0D / 16.0D;
			double d7 = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52D : d4;
			level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void openContainer(Level level, BlockPos pos, Player player) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof SulfurFurnaceBlockEntity furnace) {
			player.openMenu(furnace);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SulfurFurnaceBlockEntity(pos, state, 1);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.SULFUR_FURNACE.get(), SulfurFurnaceBlockEntity::tick);
	}
}
