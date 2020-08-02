package thebetweenlands.common.item.tools;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;

public class ItemBLFishingRod extends Item {
	@Nullable
	public EntityBLFishHook fishingHook; //shit

	public ItemBLFishingRod() {
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.TOOLS);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isFull3D() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}
	
	@Override
	   public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
		if(!isSelected && fishingHook != null)
			if (!world.isRemote) {
				fishingHook.setDead();
				fishingHook = null; //shit
			}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);

		if (fishingHook != null) {
			int i = fishingHook.reelInFishingHook();
			stack.damageItem(i, player);
			player.swingArm(handIn);
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			System.out.println("Reeling In The Years!");
			if (!world.isRemote) {
				fishingHook.setDead();
				fishingHook = null; //shit
			}
		} else {
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote) {
				System.out.println("Casting!");
				EntityBLFishHook entityfishhook = new EntityBLFishHook(world, player);
				world.spawnEntity(entityfishhook);
				fishingHook = entityfishhook; //shit
			}

			player.swingArm(handIn);
			player.addStat(StatList.getObjectUseStats(this));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}
}