package thebetweenlands.common.item.armor.amphibious;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IAmphibiousArmorAttributeUpgrade;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade;

public class AdditiveAttributeUpgrade implements IAmphibiousArmorAttributeUpgrade {
	public static final AdditiveAttributeUpgrade TOUGHNESS = new AdditiveAttributeUpgrade(SharedMonsterAttributes.ARMOR_TOUGHNESS, UUID.fromString("37b0ef4b-7e67-4734-a281-cb02d5a154a1"), "Armor toughness", 0.5D, -1);
	public static final AdditiveAttributeUpgrade KNOCKBACK_RESISTANCE = new AdditiveAttributeUpgrade(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, UUID.fromString("88d07789-7125-432c-922d-5ad82aac79a5"), "Knockback resistance", 0.25D, -1);
	public static final AdditiveAttributeUpgrade MOVEMENT_SPEED = new AdditiveAttributeUpgrade(SharedMonsterAttributes.MOVEMENT_SPEED, UUID.fromString("88457b7c-f751-40ba-8526-6d40b6df63c8"), "Movement Speed", 0.01D, 4);
	public static final AdditiveAttributeUpgrade ARMOR = new AdditiveAttributeUpgrade(SharedMonsterAttributes.ARMOR, UUID.fromString("f17c6e19-1f85-4ab9-8c90-6912b64ed518"), "generic.armor", 0.5D, -1);


	private final IAttribute attribute;

	private final UUID uuid;
	private final String name;
	private final double amount;
	private final int maxUpgradeCount;

	public AdditiveAttributeUpgrade(IAttribute attribute, UUID uuid, String name, double amount, int maxUpgradeCount) {
		this.attribute = attribute;
		this.uuid = uuid;
		this.name = name;
		this.amount = amount;
		this.maxUpgradeCount = maxUpgradeCount;
	}

	@Override
	public void applyAttributeModifiers(EntityEquipmentSlot armorType, ItemStack stack, int count, Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> counts, Multimap<String, AttributeModifier> modifiers) {
		if(maxUpgradeCount != -1) {
			count = Math.min(count, maxUpgradeCount);
		}

		modifiers.put(this.attribute.getName(), new AttributeModifier(this.uuid, this.name, this.amount * count, 0));
	}
}
