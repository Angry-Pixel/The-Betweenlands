package thebetweenlands.common.item.herblore;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemScrivenerTool extends Item {
	public ItemScrivenerTool() {
		this.setMaxDamage(256);
		this.setMaxStackSize(1);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if(facing == EnumFacing.UP && player.canPlayerEdit(pos.up(), facing, stack) && BlockRegistry.SCRIVENER_SULFUR_MARK.canPlaceBlockAt(worldIn, pos.up())) {
			ItemStack sulfurStack = ItemStack.EMPTY;

			for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack invStack = player.inventory.getStackInSlot(i);

				if(!invStack.isEmpty() && EnumItemMisc.SULFUR.isItemOf(invStack)) {
					sulfurStack = invStack;
					break;
				}
			}

			if(!sulfurStack.isEmpty()) {
				if(!worldIn.isRemote && !player.isCreative()) {
					sulfurStack.shrink(1);
					stack.damageItem(1, player);
				}

				IBlockState markState = BlockRegistry.SCRIVENER_SULFUR_MARK.getDefaultState();
				worldIn.setBlockState(pos.up(), markState);

				SoundType sound = markState.getBlock().getSoundType(markState, worldIn, pos, player);
				worldIn.playSound(player, pos, markState.getBlock().getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}
}
