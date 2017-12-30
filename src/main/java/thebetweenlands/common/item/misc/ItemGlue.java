package thebetweenlands.common.item.misc;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.util.NBTHelper;

public class ItemGlue extends Item {
	public ItemGlue() {
		this.setCreativeTab(null);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		if (playerIn.canEat(true)) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.EAT;
	}

	@Override
	@Nullable
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		stack.shrink(1);

		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer)entityLiving;
			worldIn.playSound((EntityPlayer)null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			if (!worldIn.isRemote) {
				entityPlayer.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 1));
				entityPlayer.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 200, 1));
				entityPlayer.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 2));
				entityPlayer.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 100, 0));
			}
			entityPlayer.addStat(StatList.getObjectUseStats(this));
		}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(entityIn instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityIn;
			String uuidStr = player.getUniqueID().toString();
			if("ea341fd9-27d1-4ffe-a1e0-5b05a5c8a234".equals(uuidStr)) {
				NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
				nbt.setBoolean("mmm", true);
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(stack.getTagCompound() != null && stack.getTagCompound().getBoolean("mmm")) {
			return "Sniff.. sniff... Hmm, I like this stuff";
		}
		return super.getUnlocalizedName();
	}
}
