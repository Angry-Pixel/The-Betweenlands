package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;


public class ItemChirobarbErupter extends Item {
	public final boolean electric;

	public ItemChirobarbErupter(boolean electric) {
		this.electric = electric;
		this.maxStackSize = 1;
		this.setMaxDamage(64);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.chirobarb_erupter.usage"), 0));
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		if(!world.isRemote) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (!stack.getTagCompound().hasKey("shooting"))
				stack.getTagCompound().setBoolean("shooting", false);
			if (!stack.getTagCompound().hasKey("rotation"))
				stack.getTagCompound().setInteger("rotation", 0);


			if (stack.getTagCompound().getBoolean("shooting") && entity instanceof EntityLivingBase) {
				stack.getTagCompound().setInteger("rotation", stack.getTagCompound().getInteger("rotation") + 30);

				if (stack.getTagCompound().getInteger("rotation") > 720) {
					stack.getTagCompound().setInteger("rotation", 0);
					stack.getTagCompound().setBoolean("shooting", false);
				} else if (stack.getTagCompound().getInteger("rotation") % 30 == 0) {
					EntityBLArrow arrow = new EntityBLArrow(world, (EntityLivingBase) entity);

					arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;

					arrow.setDamage(6);

					if(this.electric) {
						arrow.setType(EnumArrowType.CHIROMAW_SHOCK_BARB);
					} else {
						arrow.setType(EnumArrowType.CHIROMAW_BARB);
					}

					double angle = Math.toRadians(entity.rotationYaw + stack.getTagCompound().getInteger("rotation") - 30F);
					double dx = -Math.sin(angle);
					double dz = Math.cos(angle);
					double offsetX = dx * 1.5D;
					double offsetZ = dz * 1.5D;

					List<Entity> nearbyEntities = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(12, 0, 12), 
							e -> e instanceof EntityLivingBase && e instanceof IMob && Math.abs(e.getEntityData().getInteger("thebetweenlands.chirobarb_erupter.lastTargetted") - e.ticksExisted) >= 60);

					arrow.setPosition(entity.posX + offsetX, entity.posY + entity.height * 0.75D, entity.posZ + offsetZ);

					Entity closestNearby = null;
					double closestNearbyAngle = 0;
					double closestNearbyDstSq = Double.MAX_VALUE;

					for(Entity nearby : nearbyEntities) {
						Vec3d pos = nearby.getPositionVector().add(0, nearby.height / 2, 0);

						Vec3d diff = pos.subtract(entity.getPositionEyes(1));
						double dstSq = diff.lengthSquared();

						Vec3d dir = new Vec3d(diff.x, 0, diff.z).normalize();
						double angleDiff = Math.acos(dir.x * dx + dir.z * dz);

						if(dstSq < closestNearbyDstSq && Math.abs(diff.y) < 2 && angleDiff <= Math.toRadians(15.0f)) {
							closestNearby = nearby;
							closestNearbyDstSq = dstSq;
							Vec3d trajectory = pos.subtract(arrow.getPositionVector()).normalize();
							closestNearbyAngle = Math.toDegrees(Math.atan2(trajectory.z, trajectory.x)) - 90;
						}
					}

					float velocity = this.electric ? 1.4F : 1.1F;

					if(closestNearby != null) {
						closestNearby.getEntityData().setInteger("thebetweenlands.chirobarb_erupter.lastTargetted", closestNearby.ticksExisted);

						arrow.shoot(entity, 0F, (float)closestNearbyAngle, 1.5F, velocity, 0F);
					} else {
						arrow.shoot(entity, 0F, entity.rotationYaw + stack.getTagCompound().getInteger("rotation") - 30F, 1.5F, velocity, 0F);
					}

					world.playSound(null, entity.getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.NEUTRAL, 0.25F, 1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.8F);
					world.spawnEntity(arrow);
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
		}

		if(player.getCooldownTracker().hasCooldown(this))
			return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));

		if (!stack.getTagCompound().getBoolean("shooting")) {
			if (!world.isRemote) {
				stack.damageItem(1, player);
				player.getCooldownTracker().setCooldown(this, 60);
				stack.getTagCompound().setBoolean("shooting", true);
				stack.getTagCompound().setInteger("rotation", 0);
				world.playSound(null, player.getPosition(), SoundRegistry.CHIROBARB_ERUPTER, SoundCategory.NEUTRAL, 1F, 1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.8F);
			}
			player.swingArm(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	private static final ImmutableList<String> STACK_NBT_EXCLUSIONS = ImmutableList.of("shooting", "rotation");

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !NBTHelper.areItemStackTagsEqual(oldStack, newStack, STACK_NBT_EXCLUSIONS);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.electric;
	}
}
