package thebetweenlands.common.block.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.block.entity.MudBrickAlcoveBlockEntity;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

public class MudBrickAlcoveBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty HAS_URN = BooleanProperty.create("has_urn");
	public static final IntegerProperty MUD_LEVEL = IntegerProperty.create("mud", 0, 4);
	public static final VoxelShape ALMOST_FULL = Block.box(0.001D, 0.0D, 0.001D, 15.999D, 16.0D, 15.999D);

	public MudBrickAlcoveBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(HAS_URN, false).setValue(MUD_LEVEL, 0));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return ALMOST_FULL;
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide() && state.getValue(HAS_URN)) {
			if (level.getBlockEntity(pos) instanceof MudBrickAlcoveBlockEntity alcove) {
				BlockHitResult result = getHitResult(level, player, ClipContext.Fluid.NONE);

				if (result.getType() == HitResult.Type.BLOCK && result.getDirection() == state.getValue(FACING)) {
					BlockPos offsetPos = pos.relative(state.getValue(FACING));

					alcove.unpackLootTable(player);
					Containers.dropContents(level, pos, alcove.getItems());

					if (level.getRandom().nextInt(3) == 0) {
//						AshSprite entity = new AshSprite(level); //ash sprite here :P
//						entity.moveTo(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
//						entity.setBoundOrigin(offsetPos);
//						level.addFreshEntity(entity);
					}

					level.playSound(null, pos, state.getSoundType(level, pos, player).getBreakSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
					level.levelEvent(null, 2001, pos, Block.getId(BlockRegistry.MUD_FLOWER_POT.get().defaultBlockState())); //this will do unless we want specific particles

					level.setBlockAndUpdate(pos, state.setValue(HAS_URN, false));
					level.sendBlockUpdated(pos, state, state, 2);
					if (player instanceof ServerPlayer sp) {
						AdvancementCriteriaRegistry.BREAK_ALCOVE_URN.get().trigger(sp);
					}
				}
			}
		}
	}

	protected static BlockHitResult getHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
		Vec3 vec3 = player.getEyePosition();
		Vec3 vec31 = vec3.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(player.blockInteractionRange()));
		return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, fluidMode, player));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MudBrickAlcoveBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(HAS_URN, MUD_LEVEL));
	}
}
