package thebetweenlands.client.extensions.item.armor.util;

import java.util.EnumMap;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * A cache to more easily create custom armour models without creating a new model every single time the armour is rendered
 */
public abstract class ArmorModelCache {
	protected final EnumMap<EquipmentSlot, HumanoidModel<?>> armorModelCache = new EnumMap<>(EquipmentSlot.class);

	protected abstract HumanoidModel<?> createNewArmorModel(EquipmentSlot slot);
	
	public final HumanoidModel<?> getArmorModelForSlot(EquipmentSlot slot) {
		return this.armorModelCache.computeIfAbsent(slot, this::createNewArmorModel);
	}

	public final void clearStaleArmorModels() {
		this.armorModelCache.clear();
	}
}
