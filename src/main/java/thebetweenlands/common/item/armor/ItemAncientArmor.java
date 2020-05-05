package thebetweenlands.common.item.armor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.model.armor.ModelAncientArmor;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemAncientArmor extends ItemBLArmor {

	@SideOnly(Side.CLIENT)
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/armor/ancient.png");
	public static final ResourceLocation TEXTURE_AQUA = new ResourceLocation(ModInfo.ID, "textures/armor/ancient_aqua.png");
	public static final ResourceLocation TEXTURE_CRIMSON = new ResourceLocation(ModInfo.ID, "textures/armor/ancient_crimson.png");
	public static final ResourceLocation TEXTURE_GREEN = new ResourceLocation(ModInfo.ID, "textures/armor/ancient_green.png");

	public ItemAncientArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_ANCIENT, 3, slot, "ancient");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.ancient_armor.usage"), 0));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		CircleGemType gem = CircleGemHelper.getGem(stack);
		switch (gem) {
		case AQUA:
			return TEXTURE_AQUA.toString();
		case CRIMSON:
			return TEXTURE_CRIMSON.toString();
		case GREEN:
			return TEXTURE_GREEN.toString();
		default:
			return TEXTURE.toString();
		}
	}

	@Override
	public int getColor(ItemStack itemStack) {
		return 0xFFFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
		ModelAncientArmor model = new ModelAncientArmor();
		model.setUpModel(entityLiving);

		if (armorSlot == EntityEquipmentSlot.FEET)
			model.renderPostScaledPartBoots();

		if (armorSlot == EntityEquipmentSlot.LEGS)
			model.renderPostScaledPartLegs();

		if (armorSlot == EntityEquipmentSlot.CHEST)
			model.renderPostScaledPartChest();

		if (armorSlot == EntityEquipmentSlot.HEAD)
			model.renderPostScaledPartHelm();

		return model;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@SubscribeEvent
	public static void onEntityMagicDamage(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityLivingBase) {
			EntityLivingBase entityHit = (EntityLivingBase) event.getEntityLiving();
			if(event.getSource() == DamageSource.MAGIC) {
				float damage = 1;

				ItemStack boots = entityHit.getItemStackFromSlot(EntityEquipmentSlot.FEET);
				ItemStack legs = entityHit.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
				ItemStack chest = entityHit.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				ItemStack helm = entityHit.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

				if (!boots.isEmpty() && boots.getItem() == ItemRegistry.ANCIENT_BOOTS)
					damage -= 0.125D;
				if (!legs.isEmpty()  && legs.getItem() == ItemRegistry.ANCIENT_LEGGINGS)
					damage -= 0.125D;
				if (!chest.isEmpty() && chest.getItem() == ItemRegistry.ANCIENT_CHESTPLATE)
					damage -= 0.125D;
				if (!helm.isEmpty() && helm.getItem() == ItemRegistry.ANCIENT_HELMET)
					damage -= 0.125D;

				if (event.getAmount() * damage <= 0)
					event.setCanceled(true);
				else
					entityHit.attackEntityFrom(event.getSource(), event.getAmount() * damage);
			}
		}
	}
}
