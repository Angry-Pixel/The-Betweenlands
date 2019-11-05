package thebetweenlands.common.item.tools.bow;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityPredatorArrowGuide;

public class ItemPredatorBow extends ItemBLBow {
	public ItemPredatorBow() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	protected void fireArrow(EntityPlayer player, ItemStack stack, EntityArrow arrow, float strength) {
		arrow.motionX = arrow.motionY = arrow.motionZ = 0;
		arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, strength * 1.85F, 1.0F);
		arrow.setDamage(arrow.getDamage() + 1.05f); //Compensation for lower speed
		player.world.spawnEntity(arrow);
		
		EntityPredatorArrowGuide guide = new EntityPredatorArrowGuide(player.world);
		guide.setLocationAndAngles(arrow.posX, arrow.posY, arrow.posZ, 0, 0);
		guide.startRiding(arrow, true);
		player.world.spawnEntity(guide);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
