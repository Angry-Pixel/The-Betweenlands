package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;

public class ItemTarminion extends Item {
	public ItemTarminion() {
		this.maxStackSize = 16;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		if (!world.isRemote) {
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.NEUTRAL, 1, 1);

			EntityThrownTarminion tarminion = new EntityThrownTarminion(world, player);
			Vec3d lookVec = player.getLookVec();
			tarminion.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
			tarminion.shoot(lookVec.x, lookVec.y, lookVec.z, 0.8F, 0.1F);
			world.spawnEntity(tarminion);

			if (!player.capabilities.isCreativeMode) {
				itemStack.shrink(1);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}