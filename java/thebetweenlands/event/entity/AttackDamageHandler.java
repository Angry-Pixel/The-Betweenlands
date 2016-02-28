package thebetweenlands.event.entity;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.gemcircle.EntityGem;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.tools.ItemAxeBL;
import thebetweenlands.items.tools.ItemPickaxeBL;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.items.tools.ItemSwordBL;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.server.PacketGemProc;

public class AttackDamageHandler {
	public static final AttackDamageHandler INSTANCE = new AttackDamageHandler();

	private boolean ignoreEvent = false;

	public static final float DAMAGE_REDUCTION = 0.3F;

	@SubscribeEvent
	public void onEntityAttacked(LivingHurtEvent event) {
		if(this.ignoreEvent) return;
		EntityLivingBase attackedEntity = event.entityLiving;
		DamageSource source = event.source;
		float damage = event.ammount;

		//BL mobs overworld item resistance
		if(attackedEntity instanceof IEntityBL) {
			if (source.getSourceOfDamage() instanceof EntityPlayer) {
				EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
				ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
				if (heldItem != null) {
					boolean isUsingBLWeapon = heldItem.getItem() instanceof ItemSwordBL || heldItem.getItem() instanceof ItemAxeBL || heldItem.getItem() instanceof ItemPickaxeBL || heldItem.getItem() instanceof ItemSpadeBL;
					if (!isUsingBLWeapon) {
						damage = damage * DAMAGE_REDUCTION;
					}
				}
			}
		}

		damage = CircleGem.handleAttack(source, attackedEntity, damage);

		event.ammount = damage;
	}

	@SideOnly(Side.CLIENT)
	@SubscribePacket
	public static void onProcPacket(PacketGemProc packet) {
		byte type = packet.type;
		Entity entityHit = packet.getEntity(TheBetweenlands.proxy.getClientWorld());
		if(entityHit != null) {
			switch(type) {
			default:
				Random rnd = entityHit.worldObj.rand;
				for(int i = 0; i < 40; i++) {
					entityHit.worldObj.spawnParticle("magicCrit", 
							entityHit.posX + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width, 
							entityHit.boundingBox.minY + rnd.nextFloat() * entityHit.height, 
							entityHit.posZ + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width, 
							entityHit.motionX + rnd.nextFloat() * 0.4F - 0.2F, entityHit.motionY + rnd.nextFloat() * 0.4F - 0.2F, entityHit.motionZ + rnd.nextFloat() * 0.4F - 0.2F);
				}
			}
		}
	}
}
