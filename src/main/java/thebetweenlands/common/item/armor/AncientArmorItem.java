package thebetweenlands.common.item.armor;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorMaterial.Layer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.UnbreakableItem;
import thebetweenlands.common.registries.ArmorMaterialRegistry;

public class AncientArmorItem extends ArmorItem implements UnbreakableItem {
	public static final ResourceLocation ARMOR_TEXTURE = TheBetweenlands.prefix("textures/models/armor/ancient_layer.png");
	
	public AncientArmorItem(Type type, Properties properties) {
		super(ArmorMaterialRegistry.ANCIENT, type, properties);
	}
	
	@Override
	public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer,
			boolean innerModel) {
		return ARMOR_TEXTURE;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (UnbreakableItem.isStackBroken(stack)) {
			tooltip.add(Component.translatable("item.thebetweenlands.broken", stack.getDisplayName()).withStyle(ChatFormatting.RED));
		} else {
			tooltip.add(Component.translatable("item.thebetweenlands.ancient_armor.desc").withStyle(ChatFormatting.GRAY));
		}
	}
}
