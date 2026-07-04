package thebetweenlands.common.block.terrain;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.entity.monster.TinySludgeWorm;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import net.minecraft.world.phys.BlockHitResult;

public class DiggableSwampBlock extends Block {

    private final boolean spawnsWorms;
    private final Supplier<BlockState> digTo;

    public DiggableSwampBlock(Properties properties, Supplier<BlockState> digTo, boolean spawnsWorms) {
        super(properties);
        this.digTo = digTo;
        this.spawnsWorms = spawnsWorms;
    }

    public DiggableSwampBlock(Properties properties, boolean spawnsWorms) {
        super(properties);
        this.digTo = () -> BlockRegistry.DUG_SWAMP_DIRT.get().defaultBlockState();
        this.spawnsWorms = spawnsWorms;
    }
    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!stack.is(ItemTags.SHOVELS)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (hitResult.getDirection() != Direction.UP) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, digTo.get());

            SoundType sound = state.getSoundType(level, pos, player);
            level.playSound(null, pos, sound.getHitSound(), SoundSource.BLOCKS,
                1.0F, 0.5F + level.getRandom().nextFloat() * 0.5F);

            if (player != null) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }

            if (spawnsWorms) {
				checkForWormSpawn(level, pos, player);
            }

        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    public void checkForWormSpawn(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide && level.getDifficulty() != Difficulty.PEACEFUL) {
			if (level.random.nextInt(12) == 0) {
				TinySludgeWorm worm = new TinySludgeWorm(EntityRegistry.TINY_SLUDGE_WORM.get(), level);

                worm.setPos(pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
                level.addFreshEntity(worm);
                if (player instanceof ServerPlayer sPlayer)
                    AdvancementCriteriaRegistry.WORM_FROM_DIRT.get().trigger(sPlayer);
			}
		}
	}
}
