package thebetweenlands.common.item.armor;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.model.armor.ModelAncientArmor;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemAncientArmor extends Item3DArmor {
	public ItemAncientArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_ANCIENT, 3, slot, "ancient");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "ancient_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "ancient_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "ancient_green");
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected ModelBodyAttachment createModel() {
		return new ModelAncientArmor();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.ancient_armor.usage"), 0));
		if(stack.getItemDamage() == stack.getMaxDamage()) {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.tool.broken", stack.getDisplayName()), 0));
		}
	}

	@Override
	public int getColor(ItemStack itemStack) {
		return 0xFFFFFFFF;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
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
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		//ItemStack unspecific method can't check for damage, so just return no modifiers
		return HashMultimap.create();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		if(stack.getItemDamage() == stack.getMaxDamage()) {
			//Armor shouldn't give any reduction when fully damaged
			return HashMultimap.create();
		}
		//Returns default armor attributes
		return super.getItemAttributeModifiers(slot);
	}

	@SubscribeEvent
	public static void onEntityMagicDamage(LivingHurtEvent event) {
		if(event.getSource().isMagicDamage()) {
			float damage = 1;

			EntityLivingBase entityHit = event.getEntityLiving();

			ItemStack boots = entityHit.getItemStackFromSlot(EntityEquipmentSlot.FEET);
			ItemStack legs = entityHit.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
			ItemStack chest = entityHit.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			ItemStack helm = entityHit.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

			if (!boots.isEmpty() && boots.getItem() == ItemRegistry.ANCIENT_BOOTS && boots.getItemDamage() < boots.getMaxDamage())
				damage -= 0.125D;
			if (!legs.isEmpty()  && legs.getItem() == ItemRegistry.ANCIENT_LEGGINGS && legs.getItemDamage() < legs.getMaxDamage())
				damage -= 0.125D;
			if (!chest.isEmpty() && chest.getItem() == ItemRegistry.ANCIENT_CHESTPLATE && chest.getItemDamage() < chest.getMaxDamage())
				damage -= 0.125D;
			if (!helm.isEmpty() && helm.getItem() == ItemRegistry.ANCIENT_HELMET && helm.getItemDamage() < helm.getMaxDamage())
				damage -= 0.125D;

			event.setAmount(event.getAmount() * damage);
		}
	}
}
