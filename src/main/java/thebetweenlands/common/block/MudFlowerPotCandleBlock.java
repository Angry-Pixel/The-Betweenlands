package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.registries.BlockRegistry;

public class MudFlowerPotCandleBlock extends Block {

	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	protected static final VoxelShape POT = Block.box(5.5D, 0.0D, 5.5D, 11.5D, 7.0D, 11.5D);
	protected static final VoxelShape CANDLE = Block.box(6.5D, 7.0D, 6.5D, 10.5D, 13.0D, 10.5D);
	protected static final VoxelShape SHAPE = Shapes.or(POT, CANDLE);


	public MudFlowerPotCandleBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(LIT, true));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide()) {
			level.setBlockAndUpdate(pos, BlockRegistry.MUD_FLOWER_POT.get().defaultBlockState());
			level.playSound(null, pos, SoundType.WOOD.getBreakSound(), SoundSource.BLOCKS, (SoundType.WOOD.getVolume() + 1.0F) / 2.0F, SoundType.WOOD.getPitch() * 0.8F);
			ItemStack candle = new ItemStack(BlockRegistry.SULFUR_TORCH.asItem());
			if (player.getInventory().add(candle)) {
				player.drop(candle, false);
			}
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		state = state.cycle(LIT);
		level.setBlockAndUpdate(pos, state);
		if (state.getValue(LIT)) {
			level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.05F, 1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
		} else {
			level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT)) {
			double x = (double) pos.getX() + 0.5D;
			double y = (double) pos.getY() + 1.0D;
			double z = (double) pos.getZ() + 0.5D;

			level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
}
