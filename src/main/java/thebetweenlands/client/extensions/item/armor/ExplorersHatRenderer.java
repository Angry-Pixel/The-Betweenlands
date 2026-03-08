package thebetweenlands.client.extensions.item.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.extensions.item.armor.util.BLSimpleCustomArmorRenderer;
import thebetweenlands.client.model.armor.ExplorersHatModel;

public class ExplorersHatRenderer extends BLSimpleCustomArmorRenderer implements IClientItemExtensions {
	public static final ExplorersHatRenderer INSTANCE = new ExplorersHatRenderer();

	@Override
	protected HumanoidModel<?> createNewArmorModel(EquipmentSlot slot) {
		return new ExplorersHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.EXPLORERS_HAT));
	}
}
