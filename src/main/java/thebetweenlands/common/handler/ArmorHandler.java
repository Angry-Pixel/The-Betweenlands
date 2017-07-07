package thebetweenlands.common.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.item.armor.ItemLurkerSkinArmor;
import thebetweenlands.common.item.armor.ItemSyrmoriteArmor;

public class ArmorHandler {
	private ArmorHandler() { }

	@SubscribeEvent
	public static void onEntityDamageSource(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		EntityLivingBase entityLiving = event.getEntityLiving();

		if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA) {
			float damageMultiplier = 1;
			Iterable<ItemStack> armorStacks = entityLiving.getArmorInventoryList();
			float reductionAmount = 0.25F;
			for(ItemStack stack : armorStacks) {
				if (stack != null && stack.getItem() instanceof ItemSyrmoriteArmor) {
					damageMultiplier -= reductionAmount;
				}
			}
			if (damageMultiplier < 0.001F) {
				event.setCanceled(true);
				entityLiving.extinguish();
			} else {
				event.setAmount(damageMultiplier);
			}
		}
	}

	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		EntityPlayer player = event.getEntityPlayer();

		if(player.isInWater()) {
			IBlockState blockState = player.world.getBlockState(new BlockPos(player.posX, player.getEntityBoundingBox().maxY + 0.1D, player.posZ));
			boolean fullyInWater = blockState.getMaterial().isLiquid();

			if(fullyInWater) {
				NonNullList<ItemStack> armor = player.inventory.armorInventory;
				int pieces = 0;
				for (int i = 0; i < armor.size(); i++) {
					if (armor.get(i) != null && armor.get(i).getItem() instanceof ItemLurkerSkinArmor) {
						pieces++;
					}
				}
				if(pieces != 0) {
					event.setNewSpeed(event.getNewSpeed() * (5.0F * (player.onGround ? 1.0F : 5.0F) / 4.0F * pieces));
				}
			}
		}
	}
}