package thebetweenlands.common.items;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import thebetweenlands.common.entities.BLEntity;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ArmorMaterialRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RubberBootsItem extends ArmorItem {
	public RubberBootsItem(Properties properties) {
		super(ArmorMaterialRegistry.RUBBER, Type.LEGGINGS, properties);
	}

	public static boolean isEntityWearingRubberBoots(Entity entity) {
		return entity instanceof Player player && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RubberBootsItem;
	}

	public static boolean canEntityWalkOnMud(@Nullable Entity entity) {
		if(entity == null) return false;
		if(entity instanceof LivingEntity living && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.get().isActive(living)) return false;
		boolean canWalk = isEntityWearingRubberBoots(entity);
		boolean hasLurkerArmor = entity instanceof Player player && entity.isInWater() && player.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.LURKER_SKIN_BOOTS);
		//boolean hasUpgradedAmphibiousArmor = entity instanceof LivingEntity living && !entity.isInWater() && AmphibiousArmorItem.getUpgradeCount(living, AmphibiousArmorUpgrades.MOVEMENT_SPEED) >= 4; //TODO
		return entity.isInWater() || entity instanceof BLEntity || entity instanceof ItemEntity || canWalk || /*hasUpgradedAmphibiousArmor ||*/ hasLurkerArmor || (entity instanceof Player player && player.isCreative() && player.mayFly());
	}
}
