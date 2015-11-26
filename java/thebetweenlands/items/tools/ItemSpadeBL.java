package thebetweenlands.items.tools;

import java.util.List;

import net.minecraft.item.Item;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.utils.CorrodibleItemHelper;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ItemSpadeBL extends ItemSpade implements ICorrodible, IManualEntryItem {
	private float damageVsEntity;

	private IIcon[] corrosionIcons;

	public ItemSpadeBL(ToolMaterial material) {
		super(material);
		damageVsEntity = ReflectionHelper.getPrivateValue(ItemTool.class, this, 2);
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
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return CorrodibleItemHelper.getDigSpeed(super.getDigSpeed(stack, block, meta), stack, block, meta);
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
		return CorrodibleItemHelper.getAttributeModifiers(stack, ItemTool.field_111210_e, damageVsEntity);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		CorrodibleItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}

	@Override
	public String manualName(int meta) {
		return getToolMaterialName().toLowerCase() + "Shovel";
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