package thebetweenlands.client.item.armor.extension.util;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Creates a new model from a provided factory and model layer
 */
public class SimpleArmorModelCache extends ArmorModelCache {
	protected final BiFunction<EquipmentSlot, ModelPart, HumanoidModel<?>> modelFactory;
	protected final ModelLayerLocation modelLayer;
	
	public SimpleArmorModelCache(ModelLayerLocation modelLayer, Function<ModelPart, HumanoidModel<?>> modelFactory) {
		this(modelLayer, (slot, root) -> modelFactory.apply(root));
	}
	
	public SimpleArmorModelCache(ModelLayerLocation modelLayer, BiFunction<EquipmentSlot, ModelPart, HumanoidModel<?>> modelFactory) {
		this.modelFactory = modelFactory;
		this.modelLayer = modelLayer;
	}
	
	@Override
	protected HumanoidModel<?> createNewArmorModel(EquipmentSlot slot) {
		return this.modelFactory.apply(slot, Minecraft.getInstance().getEntityModels().bakeLayer(this.modelLayer));
	}
}
