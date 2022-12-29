package thebetweenlands.common.item.tools;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public class ItemSickle extends Item implements ICorrodible, IAnimatorRepairable {
	public ItemSickle() {
		this.setTranslationKey("thebetweenlands.sickle");
		this.setMaxStackSize(1);
		this.setMaxDamage(2500);
		this.setCreativeTab(BLCreativeTabs.GEARS);
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		boolean shouldDrop = player.world.rand.nextFloat() <= 1.0F * CorrosionHelper.getModifier(itemstack);
		if (player.world.isRemote || player.capabilities.isCreativeMode || !shouldDrop)
			return false;
		Block block = player.world.getBlockState(pos).getBlock();
		if (block instanceof ISickleHarvestable) {
			ISickleHarvestable target = (ISickleHarvestable)block;
			if (target.isHarvestable(itemstack, player.world, pos)) {
				List<ItemStack> drops = target.getHarvestableDrops(itemstack, player.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("fortune"), itemstack));
				if(drops == null || drops.isEmpty())
					return false;
				Random rand = new Random();
				for(ItemStack stack : drops) {
					float offset = 0.7F;
					double rx  = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					double ry = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					double rz = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					EntityItem entityitem = new EntityItem(player.world, (double)pos.getX() + rx, (double)pos.getY() + ry, (double)pos.getZ() + rz, stack);
					entityitem.setDefaultPickupDelay();
					player.world.spawnEntity(entityitem);
				}
				itemstack.damageItem(1, player);
				block.onBlockHarvested(player.world, pos, player.world.getBlockState(pos), player);
				if (player instanceof EntityPlayerMP)
					AdvancementCriterionRegistry.SICKLE_USE.trigger((EntityPlayerMP) player);
				player.world.setBlockToAir(pos);
				player.addStat(StatList.getBlockStats(block), 1);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return CorrosionHelper.getDestroySpeed(super.getDestroySpeed(stack, state), stack, state);
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		CorrosionHelper.addCorrosionTooltips(stack, tooltip, flagIn.isAdvanced());
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 6;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 16;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 12;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 32;
	}
}
