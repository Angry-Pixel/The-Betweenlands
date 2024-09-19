package thebetweenlands.common.items.amphibious.upgrades;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import thebetweenlands.api.item.amphibious.AmphibiousArmorAttributeUpgrade;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.common.TheBetweenlands;

import java.util.List;
import java.util.Map;

public class AdditiveAttributeUpgrade implements AmphibiousArmorAttributeUpgrade {

	public static final AdditiveAttributeUpgrade TOUGHNESS = new AdditiveAttributeUpgrade(Attributes.ARMOR_TOUGHNESS, TheBetweenlands.prefix("amphibious_toughness_upgrade"), 0.5D, -1);
	public static final AdditiveAttributeUpgrade KNOCKBACK_RESISTANCE = new AdditiveAttributeUpgrade(Attributes.KNOCKBACK_RESISTANCE, TheBetweenlands.prefix("amphibious_knockback_res_upgrade"), 0.25D, -1);
	public static final AdditiveAttributeUpgrade MOVEMENT_SPEED = new AdditiveAttributeUpgrade(Attributes.MOVEMENT_SPEED, TheBetweenlands.prefix("amphibious_speed_upgrade"), 0.01D, 4);
	public static final AdditiveAttributeUpgrade ARMOR = new AdditiveAttributeUpgrade(Attributes.ARMOR, TheBetweenlands.prefix("amphibious_armor_upgrade"), 0.5D, -1);


	private final Holder<Attribute> attribute;

	private final ResourceLocation id;
	private final double amount;
	private final int maxUpgradeCount;

	public AdditiveAttributeUpgrade(Holder<Attribute> attribute, ResourceLocation id, double amount, int maxUpgradeCount) {
		this.attribute = attribute;
		this.id = id;
		this.amount = amount;
		this.maxUpgradeCount = maxUpgradeCount;
	}

	@Override
	public void applyAttributeModifiers(ArmorItem.Type armorType, ItemStack stack, int count, List<ItemAttributeModifiers.Entry> modifiers) {
		if(this.maxUpgradeCount != -1) {
			count = Math.min(count, this.maxUpgradeCount);
		}

		modifiers.add(new ItemAttributeModifiers.Entry(this.attribute, new AttributeModifier(this.id, this.amount * count, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(armorType.getSlot())));
	}
}
