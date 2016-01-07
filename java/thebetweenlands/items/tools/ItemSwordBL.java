package thebetweenlands.items.tools;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.GemCircleHelper;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.utils.CorrodibleItemHelper;

public class ItemSwordBL extends ItemSword implements ICorrodible, IManualEntryItem {
	private IIcon[] corrosionIcons;

	private float attackDamageWeaponModifier;

	public ItemSwordBL(ToolMaterial material) {
		super(material);
		attackDamageWeaponModifier = 4 + material.getDamageVsEntity();
	}

	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase entity, EntityLivingBase player) {
		is.damageItem(1, player);
		if (is.getItem() == BLItemRegistry.octineSword)
			if (player.worldObj.rand.nextInt(GemCircleHelper.getGem(is) == CircleGem.CRIMSON ? 3 : 4) == 0)
				entity.setFire(10);
		return true;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return corrosionIcons[CorrodibleItemHelper.getCorrosionStage(stack)];
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
	public void setCorrosionIcons(IIcon[][] corrosionIcons) {
		this.corrosionIcons = corrosionIcons[0];
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrodibleItemHelper.onUpdate(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack itemStack) {
		Multimap multimap = HashMultimap.create();
		AttributeModifier attributeModifier = new AttributeModifier(field_111210_e, "Weapon modifier", attackDamageWeaponModifier * CorrodibleItemHelper.getModifier(itemStack), 0);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), attributeModifier);
		return multimap;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		CorrodibleItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}

	@Override
	public String manualName(int meta) {
		return getToolMaterialName().toLowerCase() + "Sword";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2};
	}

	@Override
	public int metas() {
		return 0;
	}
}