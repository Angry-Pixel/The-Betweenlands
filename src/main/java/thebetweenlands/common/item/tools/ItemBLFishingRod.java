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
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.util.NBTHelper;
import thebetweenlands.util.TranslationHelper;

public class ItemBLFishingRod extends Item {

	public ItemBLFishingRod() {
		setMaxDamage(128);
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
					return (mainHand || offHand) && entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).fishEntity != null && ((EntityPlayer)entityIn).fishEntity instanceof EntityBLFishHook ? 1.0F : 0.0F;
				}
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("baited")) {
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
			if (!stack.getTagCompound().hasKey("baited"))
				stack.getTagCompound().setBoolean("baited", false);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);

		if(!world.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("baited"))
				stack.getTagCompound().setBoolean("baited", false);
		}

		if (player.fishEntity != null) {
			int i = player.fishEntity.handleHookRetraction();
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			//TODO
			//else play creaking sound

			if (!world.isRemote && player.fishEntity != null) {
				//fixes stupid entity MobItem still being ridden after netted
				if(player.fishEntity.caughtEntity != null && !player.fishEntity.isRiding())
					player.fishEntity.caughtEntity = null;
				
				if (player.fishEntity.caughtEntity != null && stack.getTagCompound().hasKey("baited")) {
					if (stack.getTagCompound().getBoolean("baited"))
						stack.getTagCompound().setBoolean("baited", false);
					if (((EntityAnadia) player.fishEntity.caughtEntity).getStaminaTicks() % 20 == 0 && ((EntityAnadia) player.fishEntity.caughtEntity).getStaminaTicks() != 0) {
						stack.damageItem(i, player);
					}
				}
				if (player.fishEntity.caughtEntity == null && (int)player.fishEntity.getDistance(player.fishEntity.getAngler()) > 0)
					stack.damageItem(i, player);

				if ((int)player.fishEntity.getDistance(player.fishEntity.getAngler()) <= 0 && !player.fishEntity.isRiding()) {
					player.fishEntity.setDead();
				}
			}
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		} else {
			world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote && player.fishEntity == null) {
				EntityBLFishHook entityFishHook = new EntityBLFishHook(world, player);
				if(stack.getTagCompound().getBoolean("baited"))
					entityFishHook.setBaited(true);
				world.spawnEntity(entityFishHook);
			}

			player.swingArm(handIn);
			player.addStat(StatList.getObjectUseStats(this));
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("baited");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}
}