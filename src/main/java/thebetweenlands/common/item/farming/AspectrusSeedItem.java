package thebetweenlands.common.item.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.farming.AspectrusCropBlock;
import thebetweenlands.common.block.farming.DugSoilBlock;
import thebetweenlands.common.block.farming.RubberTreeFenceBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class AspectrusSeedItem extends Item {


    public AspectrusSeedItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedBlockState = level.getBlockState(clickedPos);
        BlockPos cropPos;
        BlockState dugSoilBlockState;
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (clickedBlockState.getBlock() instanceof DugSoilBlock && (context.getClickedFace() == net.minecraft.core.Direction.UP) && clickedBlockState.getValue(DugSoilBlock.COMPOSTED)) {
            cropPos = clickedPos.above();
            BlockState fenceBlockState = level.getBlockState(cropPos);
            if (!(fenceBlockState.getBlock() instanceof RubberTreeFenceBlock)) return InteractionResult.FAIL;
            dugSoilBlockState = clickedBlockState;
            // if (player != null && !player.mayUseItemAt(clickedPos, context.getClickedFace(), stack)) {
            //     return InteractionResult.FAIL;
            // }
        } else if (clickedBlockState.getBlock() instanceof RubberTreeFenceBlock) {
            cropPos = clickedPos;
            dugSoilBlockState = level.getBlockState(cropPos.below());
            if (!(dugSoilBlockState.getBlock() instanceof DugSoilBlock)) return InteractionResult.FAIL;
            // if (player != null && !player.mayUseItemAt(cropPos, context.getClickedFace(), stack)) {
            //     return InteractionResult.FAIL;
            // }
        } else return InteractionResult.FAIL;
        
        BlockState crop = BlockRegistry.ASPECTRUS_CROP.get().defaultBlockState()
                .setValue(AspectrusCropBlock.DECAYED, dugSoilBlockState.getValue(DugSoilBlock.DECAYED));

        if (!level.isClientSide()) {
            level.setBlock(cropPos, crop, 3);
            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}