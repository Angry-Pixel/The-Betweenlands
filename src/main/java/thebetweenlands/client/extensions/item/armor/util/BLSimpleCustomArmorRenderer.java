package thebetweenlands.client.extensions.item.armor.util;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * Version of {@linkplain ArmorModelCache} that is also a {@linkplain IClientItemExtensions} for easy extension
 */
public abstract class BLSimpleCustomArmorRenderer extends ArmorModelCache implements IClientItemExtensions, ResourceManagerReloadListener {

	@Override
	public final HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
		return this.getArmorModelForSlot(slot);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		this.clearStaleArmorModels();
	}
}
