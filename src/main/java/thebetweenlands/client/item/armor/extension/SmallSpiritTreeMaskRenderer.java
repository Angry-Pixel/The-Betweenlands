package thebetweenlands.client.item.armor.extension;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.item.armor.extension.util.SimpleArmorModelCache;
import thebetweenlands.common.TheBetweenlands;

public class SmallSpiritTreeMaskRenderer extends MaskRenderer implements ResourceManagerReloadListener {
	public static final SmallSpiritTreeMaskRenderer INSTANCE = new SmallSpiritTreeMaskRenderer();

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/misc/small_spirit_tree_mask.png");
	private static final ResourceLocation SIDE_TEXTURE = TheBetweenlands.prefix("textures/misc/small_spirit_tree_mask_side.png");

	private final SimpleArmorModelCache armorModelCache = new SimpleArmorModelCache(BLModelLayers.SMALL_SPIRIT_TREE_MASK, HumanoidArmorModel::new);
	
	@Override
	public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
		return this.armorModelCache.getArmorModelForSlot(slot);
	}

	@Override
	public ResourceLocation getOverlayTexture() {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getSideOverlayTexture(boolean left) {
		return SIDE_TEXTURE;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		this.armorModelCache.clearStaleArmorModels();
	}
}
