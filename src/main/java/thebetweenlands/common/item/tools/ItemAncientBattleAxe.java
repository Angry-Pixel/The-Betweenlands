package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemAncientBattleAxe extends ItemGreataxe {
	public ItemAncientBattleAxe(ToolMaterial material) {
		super(material);
		this.setMaxDamage(material.getMaxUses());
	}

	public ItemAncientBattleAxe() {
		this(BLMaterialRegistry.TOOL_VALONITE);
	}

	@Override
	protected double getBlockBreakReach(EntityLivingBase entity, ItemStack stack) {
		return stack.getItemDamage() == stack.getMaxDamage() ? 0.0D : 3.0D;
	}

	@Override
	protected double getBlockBreakHalfAngle(EntityLivingBase entity, ItemStack stack) {
		return stack.getItemDamage() == stack.getMaxDamage() ? 0.0D : 55.0D;
	}

	@Override
	protected float getSwingSpeedMultiplier(EntityLivingBase entity, ItemStack stack) {
		return 0.225F;
	}

	@Override
	protected double getAoEReach(EntityLivingBase entityLiving, ItemStack stack) {
		return 2.2D;
	}

	@Override
	public double getReach() {
		return 3.5D;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
		if(equipmentSlot == EntityEquipmentSlot.MAINHAND && stack.getItemDamage() == stack.getMaxDamage()) {
			Multimap<String, AttributeModifier> map = HashMultimap.<String, AttributeModifier>create();
			map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", -1, 1));
			return map;
		}

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

		if(equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.0D, 0));
		}

		return multimap;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(stack.getItemDamage() == stack.getMaxDamage()) {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.tool.broken", stack.getDisplayName()), 0));
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the sword break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_LOOT);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
