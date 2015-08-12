package thebetweenlands.items;

import java.util.List;

import net.minecraftforge.common.ForgeHooks;
import thebetweenlands.recipes.BLMaterials;
import thebetweenlands.utils.DecayableItemHelper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class PickaxeBL extends ItemPickaxe implements IDecayable {
	private float damageVsEntity;

	private IIcon[] decayIcons;

	public PickaxeBL(ToolMaterial material) {
		super(material);
		damageVsEntity = ReflectionHelper.getPrivateValue(ItemTool.class, this, 2);
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
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return DecayableItemHelper.getDigSpeed(super.getDigSpeed(stack, block, meta), stack, block, meta);
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
        return DecayableItemHelper.getAttributeModifiers(stack, ItemTool.field_111210_e, damageVsEntity);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		DecayableItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}

}