package thebetweenlands.common.item.misc;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityGlowingGoop;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGlowingGoop extends ItemBlock {
	public ItemGlowingGoop() {
		super(BlockRegistry.GLOWING_GOOP);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("tooltip.bl.item.glowing_goop"));
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(!canUse(playerIn.getHeldItem(handIn))) {
			return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
		}

		playerIn.setActiveHand(handIn);

		return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (player instanceof EntityPlayer && canUse(stack)) {
			Vec3d forward = player.getLookVec();
			float yaw = player.rotationYaw;
			float pitch = player.rotationPitch - 90;
			float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
			float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
			float f2 = -MathHelper.cos(-pitch * 0.017453292F);
			float f3 = MathHelper.sin(-pitch * 0.017453292F);
			Vec3d up = new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
			Vec3d right = forward.crossProduct(up).normalize();
			Vec3d source = player.getPositionVector().add(0, player.getEyeHeight() - 0.2F, 0).add(forward.scale(0.4F)).add(right.scale(0.3F));
			player.world.spawnParticle(EnumParticleTypes.SLIME, source.x + player.world.rand.nextFloat() * 0.5F - 0.25F, source.y + player.world.rand.nextFloat() * 0.5F - 0.25F, source.z + player.world.rand.nextFloat() * 0.5F - 0.25F, 0, 0, 0);
		}

		super.onUsingTick(stack, player, count);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
		if(!canUse(stack))
			return;

		if (!world.isRemote && entityLiving instanceof EntityPlayer) {
			int useTime = this.getMaxItemUseDuration(stack) - timeLeft;

			if(useTime > 20) {
				EntityGlowingGoop goop = new EntityGlowingGoop(world, entityLiving);
				goop.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, -10, 1.2F, 3.5F);
				world.spawnEntity(goop);
				world.playSound(null, entityLiving.getPosition(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 1F, 1F);
				if(!((EntityPlayer)entityLiving).isCreative()) {
					stack.shrink(1);
				}
			}
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if(!canUse(stack)) {
			return false;
		}

		Block block = this.block;
		
		if(world.getBlockState(pos).getMaterial() == Material.WATER) {
			block = BlockRegistry.GLOWING_GOOP_UNDERWATER;
			newState = block.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, 0, player, EnumHand.MAIN_HAND);
		}

		if(!world.setBlockState(pos, newState, 11)) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == block) {
			setTileEntityNBT(world, player, pos, stack);
			this.block.onBlockPlacedBy(world, pos, state, player, stack);

			if(player instanceof EntityPlayerMP) {
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
			}
		}

		return true;
	}


	private boolean canUse(ItemStack stack) {
		return ItemAmphibiousArmor.getUpgradeItemStoredDamage(stack) == 0;
	}
}