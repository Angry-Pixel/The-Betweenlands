package thebetweenlands.event.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.items.tools.ItemAxeBL;
import thebetweenlands.items.tools.ItemPickaxeBL;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.items.tools.ItemSwordBL;

public class AttackDamageHandler {
	public static final AttackDamageHandler INSTANCE = new AttackDamageHandler();

	private boolean ignoreEvent = false;

	public static final float DAMAGE_REDUCTION = 0.3F;

	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event) {
		if(this.ignoreEvent) return;
		EntityLivingBase attackedEntity = event.entityLiving;
		DamageSource source = event.source;
		float damage = event.ammount;
		if(attackedEntity instanceof IEntityBL) {
			if (source.getSourceOfDamage() instanceof EntityPlayer) {
				EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
				ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
				if (heldItem != null) {
					boolean isUsingBLWeapon = heldItem.getItem() instanceof ItemSwordBL || heldItem.getItem() instanceof ItemAxeBL || heldItem.getItem() instanceof ItemPickaxeBL || heldItem.getItem() instanceof ItemSpadeBL;
					if (!isUsingBLWeapon) {
						this.ignoreEvent = true;
						attackedEntity.attackEntityFrom(source, MathHelper.ceiling_float_int(damage * DAMAGE_REDUCTION));
						this.ignoreEvent = false;
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
