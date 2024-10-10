package thebetweenlands.common.item.armor;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.common.NeoForgeMod;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.ArmorMaterialRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RubberBootsItem extends ArmorItem {
	public RubberBootsItem(Properties properties) {
		super(ArmorMaterialRegistry.RUBBER, Type.BOOTS, properties);
	}

	public static boolean isEntityWearingRubberBoots(Entity entity) {
		return entity instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem;
	}

	public static boolean canEntityWalkOnMud(@Nullable Entity entity) {
		if (entity == null) return true; //prevents entities from clipping into the blocks when spawned on them
		if (entity instanceof LivingEntity living && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.get().isActive(living)) return false;
		if (entity instanceof BLEntity || entity instanceof ItemEntity) return true;
		if (entity instanceof Player player && (player.isCreative() || player.mayFly())) return true;
		if (entity.isInFluidType(FluidTypeRegistry.SWAMP_WATER.get()) || entity.isInFluidType(NeoForgeMod.WATER_TYPE.value())) return true;
		boolean canWalk = isEntityWearingRubberBoots(entity);
		boolean hasLurkerArmor = entity instanceof Player player && entity.isInWater() && player.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.LURKER_SKIN_BOOTS);
		boolean hasUpgradedAmphibiousArmor = entity instanceof LivingEntity living && !entity.isInWater() && AmphibiousArmorItem.getUpgradeCount(living, AmphibiousArmorUpgradeRegistry.MOVEMENT_SPEED) >= 4;
		return canWalk || hasUpgradedAmphibiousArmor || hasLurkerArmor;
	}
}
