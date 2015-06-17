package thebetweenlands.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.utils.DecayableItemHelper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SwordBL extends ItemSword implements IDecayable {
	private IIcon[] decayIcons;

	private float attackDamageWeaponModifier;

	public SwordBL(ToolMaterial material) {
		super(material);
		attackDamageWeaponModifier = 4 + material.getDamageVsEntity();
	}

	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase entity, EntityLivingBase player) {
		is.damageItem(1, player);
		if (is.getItem() == BLItemRegistry.octineSword)
			if (player.worldObj.rand.nextInt(4) == 0)
				entity.setFire(10);
		return true;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return decayIcons[DecayableItemHelper.getDecayStage(stack)];
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	@Override
	public IIcon[] getIcons() {
		return new IIcon[] { itemIcon };
	}

	@Override
	public void setDecayIcons(IIcon[][] decayIcons) {
		this.decayIcons = decayIcons[0];
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		DecayableItemHelper.onUpdate(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack itemStack) {
		Multimap multimap = HashMultimap.create();
		AttributeModifier attributeModifier = new AttributeModifier(field_111210_e, "Weapon modifier", attackDamageWeaponModifier * DecayableItemHelper.getModifier(itemStack), 0);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), attributeModifier);
		return multimap;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		DecayableItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}
}