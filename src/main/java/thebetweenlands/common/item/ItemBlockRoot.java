package thebetweenlands.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemBlockRoot extends ItemBlock {
	public ItemBlockRoot() {
		super(BlockRegistry.ROOT);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            pos = pos.offset(facing);
        }
        
        Block toPlace = this.block;
        
        if(world.getBlockState(pos).getMaterial() == Material.WATER) {
        	toPlace = BlockRegistry.ROOT_UNDERWATER;
        }

        ItemStack stack = player.getHeldItem(hand);

        if(!stack.isEmpty() && player.canPlayerEdit(pos, facing, stack) && world.mayPlace(toPlace, pos, false, facing, (Entity)null)) {
            int meta = this.getMetadata(stack.getMetadata());
            IBlockState state = toPlace.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);

            if (placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, state)) {
                state = world.getBlockState(pos);
                SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                stack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
