package thebetweenlands.items.armor;

import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.BLMaterial;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.IManualEntryItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRubberBoots extends ItemArmorBL implements IManualEntryItem {
	private static final int MAX_WALK_TICKS = 40;

	public ItemRubberBoots() {
		super(BLMaterial.armorRubber, 2, 3, "thebetweenlands:textures/armour/rubberBoots.png", null);
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.RUBBER_BALL.id;
	}
	
	@Override
	public String manualName(int meta) {
		return "rubberBoots";
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