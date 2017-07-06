package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemAngryPebble extends Item {
	public ItemAngryPebble() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStackIn = player.getHeldItem(hand);
		if (!player.capabilities.isCreativeMode) {
			itemStackIn.shrink(1);
		}

		if (!world.isRemote) {
			world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.SORRY, SoundCategory.PLAYERS, 0.7F, 0.8F);
			EntityAngryPebble pebble = new EntityAngryPebble(world, player);
			pebble.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, -10, 1.2F, 3.5F);
			world.spawnEntity(pebble);
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
}