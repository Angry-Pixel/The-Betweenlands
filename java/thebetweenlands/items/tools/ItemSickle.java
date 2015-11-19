package thebetweenlands.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.utils.CorrodibleItemHelper;

public class ItemSickle extends Item implements ICorrodible {
	private IIcon[] corrosionIcons;

	public ItemSickle() {
		this.setUnlocalizedName("thebetweenlands.sickle");
		this.setTextureName("thebetweenlands:strictlyHerblore/tools/sickle");
		this.setMaxStackSize(1);
		this.setMaxDamage(2500);
		ModCreativeTabs.herbLore.setTab(this);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
		boolean shouldDrop = player.worldObj.rand.nextFloat() <= 1.0F * CorrodibleItemHelper.getModifier(itemstack);
		if (player.worldObj.isRemote || player.capabilities.isCreativeMode || !shouldDrop) {
			return false;
		}
		Block block = player.worldObj.getBlock(x, y, z);
		if (block instanceof IHarvestable) {
			IHarvestable target = (IHarvestable)block;
			if (target.isHarvestable(itemstack, player.worldObj, x, y, z)) {
				ArrayList<ItemStack> drops = target.getHarvestableDrops(itemstack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
				Random rand = new Random();
				for(ItemStack stack : drops) {
					float f = 0.7F;
					double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				}
				itemstack.damageItem(1, player);
				player.worldObj.setBlock(x, y, z, Blocks.air);
				player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
				return true;
			}
		}
		return false;
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
		return new IIcon[] { this.itemIcon };
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
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		CorrodibleItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}
}
