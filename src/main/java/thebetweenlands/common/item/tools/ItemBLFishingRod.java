package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.util.NBTHelper;
import thebetweenlands.util.TranslationHelper;

public class ItemBLFishingRod extends Item {
	@Nullable
	public EntityBLFishHook fishingHook;

	public ItemBLFishingRod() {
		setMaxDamage(64);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.TOOLS);

		addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == null) {
					return 0.0F;
				} else {
					boolean mainHand = entityIn.getHeldItemMainhand() == stack;
					boolean offHand = entityIn.getHeldItemOffhand() == stack;
					if (entityIn.getHeldItemMainhand().getItem() instanceof ItemBLFishingRod)
						offHand = false;
					return (mainHand || offHand) && entityIn instanceof EntityPlayer && (stack.hasTagCompound() && stack.getTagCompound().hasKey("cast") && stack.getTagCompound().getBoolean("cast")) ? 1.0F : 0.0F;
				}
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("cast")) {
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fishing_rod.baited", stack.getTagCompound().getBoolean("baited")));
		}
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
		if(!world.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("cast"))
				stack.getTagCompound().setBoolean("cast", false);
			if (!stack.getTagCompound().hasKey("baited"))
				stack.getTagCompound().setBoolean("baited", false);
		}

		if(!isSelected && fishingHook != null)
			if (!world.isRemote) {
				fishingHook.setDead();
				fishingHook = null;
				stack.getTagCompound().setBoolean("cast", false);
			}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);

		if(!world.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("cast"))
				stack.getTagCompound().setBoolean("cast", false);
			if (!stack.getTagCompound().hasKey("baited"))
				stack.getTagCompound().setBoolean("baited", false);
		}

		if (fishingHook != null) {
			int i = fishingHook.reelInFishingHook();
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			//TODO
			//else play creaking sound

			//fixes stupid entity
			if(fishingHook.caughtEntity != null && !fishingHook.isRiding())
				fishingHook.caughtEntity = null;
			
			if(fishingHook.caughtEntity != null && stack.getTagCompound().getBoolean("baited")) {
				//removes bait from hook
				if (!world.isRemote )
					stack.getTagCompound().setBoolean("baited", false);
			}

			if (!world.isRemote && (int)fishingHook.getDistance(fishingHook.getAngler()) <= 0 && !fishingHook.isRiding() || !stack.getTagCompound().getBoolean("cast")) {
				stack.damageItem(i, player);
				fishingHook.setDead();
				fishingHook = null;
				stack.getTagCompound().setBoolean("cast", false);
			}
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		} else {
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote) {
				stack.getTagCompound().setBoolean("cast", true);
				EntityBLFishHook entityfishhook = new EntityBLFishHook(world, player);
				if(stack.getTagCompound().getBoolean("baited"))
					entityfishhook.setBaited(true);
				world.spawnEntity(entityfishhook);
				fishingHook = entityfishhook;
			}

			player.swingArm(handIn);
			player.addStat(StatList.getObjectUseStats(this));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("cast", "baited");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}
}