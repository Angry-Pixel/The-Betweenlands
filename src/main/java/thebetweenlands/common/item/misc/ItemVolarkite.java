package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityVolarkite;

public class ItemVolarkite extends Item {
	public ItemVolarkite() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);

		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			if(!player.isRiding()) {
				EntityVolarkite entity = new EntityVolarkite(world);
				entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, 0);
				entity.motionX = player.motionX;
				entity.motionY = player.motionY;
				entity.motionZ = player.motionZ;
				entity.velocityChanged = true;

				world.spawnEntity(entity);

				player.startRiding(entity);
			}
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
