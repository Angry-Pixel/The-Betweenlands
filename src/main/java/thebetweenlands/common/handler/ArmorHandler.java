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
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorUpgrades;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;

public class ArmorHandler {
	private ArmorHandler() { }

	@SubscribeEvent
	public static void onEntityOnFire(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		EntityLivingBase entityLiving = event.getEntityLiving();

		if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA) {
			float damageMultiplier = 1;
			Iterable<ItemStack> armorStacks = entityLiving.getArmorInventoryList();
			float reductionAmount = 0.25F;
			for(ItemStack stack : armorStacks) {
				if (!stack.isEmpty() && stack.getItem() instanceof ItemSyrmoriteArmor) {
					damageMultiplier -= reductionAmount;
				}
			}
			if (damageMultiplier < 0.001F) {
				event.setAmount(0.01F); //Set to tiny amount so armor still takes damage
				entityLiving.extinguish();
			} else {
				event.setAmount(event.getAmount() * damageMultiplier);
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
				boolean fullSetAmphibious = true;

				for (int i = 0; i < armor.size(); i++) {
					if (!armor.get(i).isEmpty() && armor.get(i).getItem() instanceof ItemLurkerSkinArmor) {
						fullSetAmphibious = false;
						pieces++;
					} else if (!(armor.get(i).getItem() instanceof ItemAmphibiousArmor)) {
						fullSetAmphibious = false;
					}
				}

				// only give full mining speed if we have full amphibious set + the mining upgrade
				if(pieces == 0 && fullSetAmphibious && ItemAmphibiousArmor.getUpgradeCount(player, AmphibiousArmorUpgrades.MINING_SPEED) > 0) {
					pieces = 4;
				}

				if(pieces != 0) {
					event.setNewSpeed(event.getNewSpeed() * (5.0F * (player.onGround ? 1.0F : 5.0F) / 4.0F * pieces));
				}
			}
		}
	}
}