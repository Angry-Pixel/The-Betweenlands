package thebetweenlands.common.item.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorUpgrades;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;


public class ItemRubberBoots extends ItemBLArmor {
	public ItemRubberBoots() {
		super(BLMaterialRegistry.ARMOR_RUBBER, 3, EntityEquipmentSlot.FEET, "rubber_boots");
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return EnumItemMisc.RUBBER_BALL.isItemOf(material);
	}

	public static boolean isEntityWearingRubberBoots(Entity entity) {
		return entity instanceof EntityPlayer && !((EntityPlayer)entity).inventory.armorInventory.get(0).isEmpty() && ((EntityPlayer)entity).inventory.armorInventory.get(0).getItem() instanceof ItemRubberBoots;
	}

	public static boolean canEntityWalkOnMud(Entity entity) {
		if(entity instanceof EntityLivingBase && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive((EntityLivingBase)entity)) return false;
		boolean canWalk = isEntityWearingRubberBoots(entity);
		boolean hasLurkerArmor = entity instanceof EntityPlayer && entity.isInWater() && !((EntityPlayer) entity).inventory.armorInventory.get(0).isEmpty() && ((EntityPlayer) entity).inventory.armorInventory.get(0).getItem() == ItemRegistry.LURKER_SKIN_BOOTS;
		boolean hasUpgradedAmphibiousArmor = entity instanceof EntityLivingBase && !entity.isInWater() && ItemAmphibiousArmor.getUpgradeCount((EntityLivingBase)entity, AmphibiousArmorUpgrades.MOVEMENT_SPEED) >= 4;
		return entity.isInWater() || entity instanceof IEntityBL || entity instanceof EntityItem || canWalk || hasUpgradedAmphibiousArmor || hasLurkerArmor || (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode && ((EntityPlayer)entity).capabilities.isFlying);
	}
}
