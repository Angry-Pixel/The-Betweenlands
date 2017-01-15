package thebetweenlands.common.item.food;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemTangledRoot extends Item {
	public ItemTangledRoot() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (player.canEat(true)) {
			player.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.EAT;
	}

	@Nullable
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		--stack.stackSize;

		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer)entityLiving;
			worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			if (!worldIn.isRemote) {
				entityplayer.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
			}
			entityplayer.addStat(StatList.getObjectUseStats(this));
		}

		return stack;
	}
}
