package thebetweenlands.common.item.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemHagHacker extends ItemLootSword {
	public ItemHagHacker() {
		super(BLMaterialRegistry.TOOL_LOOT);
		this.addInstantKills(EntitySwampHag.class);
		this.setMaxDamage(32);
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if(equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.0D, 0));
		}
		return multimap;
	}
}