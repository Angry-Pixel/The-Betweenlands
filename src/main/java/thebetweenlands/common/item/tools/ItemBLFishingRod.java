package thebetweenlands.common.item.tools;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class ItemBLFishingRod extends Item {
	@Nullable
	public EntityBLFishHook fishEntity; //shit

	public ItemBLFishingRod() {
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == null) {
					return 0.0F;
				} else {
					boolean flag = entityIn.getHeldItemMainhand() == stack;
					boolean flag1 = entityIn.getHeldItemOffhand() == stack;

					if (entityIn.getHeldItemMainhand().getItem() instanceof ItemBLFishingRod) {
						flag1 = false;
					}

					return (flag || flag1) && entityIn instanceof EntityPlayer && fishEntity != null ? 1.0F : 0.0F;
				}
			}
		});
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
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (fishEntity != null) {
			int i = fishEntity.handleHookRetraction();
			stack.damageItem(i, playerIn);
			playerIn.swingArm(handIn);
			world.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			fishEntity = null; //shit
		} else {
			world.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!world.isRemote) {
				EntityBLFishHook entityfishhook = new EntityBLFishHook(world, playerIn);
				int j = EnchantmentHelper.getFishingSpeedBonus(stack);

				if (j > 0) {
					entityfishhook.setLureSpeed(j);
				}

				int k = EnchantmentHelper.getFishingLuckBonus(stack);

				if (k > 0) {
					entityfishhook.setLuck(k);
				}

				world.spawnEntity(entityfishhook);
				fishEntity = entityfishhook; //shit
			}

			playerIn.swingArm(handIn);
			playerIn.addStat(StatList.getObjectUseStats(this));
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}
}