package thebetweenlands.common.item.armor.amphibian;

import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IAmphibianArmorAttributeUpgrade;

public class AdditiveAttributeUpgrade implements IAmphibianArmorAttributeUpgrade {
	public static final AdditiveAttributeUpgrade TOUGHNESS = new AdditiveAttributeUpgrade(SharedMonsterAttributes.ARMOR_TOUGHNESS, UUID.fromString("37b0ef4b-7e67-4734-a281-cb02d5a154a1"), "Armor toughness", 0.5D);
	public static final AdditiveAttributeUpgrade KNOCKBACK_RESISTANCE = new AdditiveAttributeUpgrade(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, UUID.fromString("88d07789-7125-432c-922d-5ad82aac79a5"), "Knockback resistance", 0.25D);

	private final IAttribute attribute;

	private final UUID uuid;
	private final String name;
	private final double amount;

	public AdditiveAttributeUpgrade(IAttribute attribute, UUID uuid, String name, double amount) {
		this.attribute = attribute;
		this.uuid = uuid;
		this.name = name;
		this.amount = amount;
	}

	@Override
	public void applyAttributeModifiers(EntityEquipmentSlot armorType, ItemStack stack, int count, Multimap<String, AttributeModifier> modifiers) {
		modifiers.put(this.attribute.getName(), new AttributeModifier(this.uuid, this.name, this.amount * count, 0));
	}
}
