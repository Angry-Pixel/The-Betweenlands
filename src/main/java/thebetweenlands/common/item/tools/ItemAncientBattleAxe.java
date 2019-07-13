package thebetweenlands.common.item.tools;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemAncientBattleAxe extends ItemGreataxe {
	public ItemAncientBattleAxe(ToolMaterial material) {
		super(material);
	}

	public ItemAncientBattleAxe() {
		this(BLMaterialRegistry.TOOL_VALONITE);
	}

	@Override
	protected double getBlockBreakReach(EntityLivingBase entity, ItemStack stack) {
		return 3.0D;
	}

	@Override
	protected double getBlockBreakHalfAngle(EntityLivingBase entity, ItemStack stack) {
		return 55.0D;
	}

	@Override
	protected float getSwingSpeedMultiplier(EntityLivingBase entity, ItemStack stack) {
		return 0.225F;
	}

	@Override
	protected double getAoEReach(EntityLivingBase entityLiving, ItemStack stack) {
		return 1.9D;
	}

	@Override
	public double getReach() {
		return 3.5D;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

		if(equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.2D, 0));
		}

		return multimap;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
