package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelAmphibianArmor;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemAmphibianArmor extends Item3DArmor {
	public ItemAmphibianArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_AMPHIBIAN, 3, slot, "amphibian");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "amphibian_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "amphibian_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "amphibian_green");
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected ModelBodyAttachment createModel() {
		return new ModelAmphibianArmor();
	}
}
