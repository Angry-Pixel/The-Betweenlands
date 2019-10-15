package thebetweenlands.common.item.misc;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityGalleryFrame;

public class ItemGalleryFrame extends Item {
	public final EntityGalleryFrame.Type type;

	public ItemGalleryFrame(EntityGalleryFrame.Type type) {
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.type = type;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack is, ItemStack book) {
		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = player.getHeldItem(hand);
		BlockPos offsetPos = pos.offset(facing);

		if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(offsetPos, facing, itemstack)) {
			EntityHanging entity = new EntityGalleryFrame(world, offsetPos, facing, this.type);

			if (entity != null && entity.onValidSurface()) {
				if (!world.isRemote) {
					entity.playPlaceSound();
					world.spawnEntity(entity);
				}

				itemstack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}
}
