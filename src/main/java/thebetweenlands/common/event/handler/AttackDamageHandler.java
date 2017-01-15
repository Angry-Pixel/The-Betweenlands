package thebetweenlands.common.event.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.entity.mobs.IEntityBL;
import thebetweenlands.common.item.tools.ItemBLAxe;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;

public class AttackDamageHandler {
	public static final float DAMAGE_REDUCTION = 0.3F;

	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		EntityLivingBase attackedEntity = event.getEntityLiving();
		DamageSource source = event.getSource();
		float damage = event.getAmount();

		if(attackedEntity instanceof IEntityBL) {
			if (source.getSourceOfDamage() instanceof EntityPlayer) {
				//Cap damage of overly OP weapons
				damage = Math.min(damage, 40.0F);

				//BL mobs overworld item resistance
				EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
				ItemStack heldItem = entityPlayer.getActiveItemStack();
				if (heldItem != null) {
					boolean isUsingBLWeapon = heldItem.getItem() instanceof ItemBLSword || heldItem.getItem() instanceof ItemBLAxe || heldItem.getItem() instanceof ItemBLPickaxe || heldItem.getItem() instanceof ItemBLShovel;
					if (!isUsingBLWeapon) {
						damage = damage * DAMAGE_REDUCTION;
					}
				}
			}
		}

		damage = CircleGemHelper.handleAttack(source, attackedEntity, damage);

		event.setAmount(damage);
	}
}
