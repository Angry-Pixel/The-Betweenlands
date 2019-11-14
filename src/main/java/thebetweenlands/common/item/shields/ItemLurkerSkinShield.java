package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.entity.EntityLurkerSkinRaft;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemLurkerSkinShield extends ItemBLShield {
	public ItemLurkerSkinShield() {
		super(BLMaterialRegistry.TOOL_LURKER_SKIN);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.lurker_skin_shield"), 0));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		float pitch = playerIn.rotationPitch;
		float yaw = playerIn.rotationYaw;
		double playerX = playerIn.posX;
		double playerY = playerIn.posY + (double)playerIn.getEyeHeight();
		double playerZ = playerIn.posZ;
		Vec3d playerPos = new Vec3d(playerX, playerY, playerZ);
		float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
		float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
		float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
		float pitchSin = MathHelper.sin(-pitch * 0.017453292F);
		float dirX = yawSin * pitchCos;
		float dirZ = yawCos * pitchCos;
		Vec3d endPos = playerPos.add((double)dirX * 5.0D, (double)pitchSin * 5.0D, (double)dirZ * 5.0D);
		RayTraceResult rayTrace = worldIn.rayTraceBlocks(playerPos, endPos, true);

		if(rayTrace != null) {
			Vec3d lookVec = playerIn.getLook(1.0F);
			boolean entityColliding = false;
			List<Entity> entities = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getEntityBoundingBox().expand(lookVec.x * 5.0D, lookVec.y * 5.0D, lookVec.z * 5.0D).grow(1.0D));

			for(Entity entity : entities) {
				if(entity.canBeCollidedWith()) {
					AxisAlignedBB aabb = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());

					if(aabb.contains(playerPos)) {
						entityColliding = true;
						break;
					}
				}
			}

			if(!entityColliding && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
				boolean isWater = worldIn.getBlockState(rayTrace.getBlockPos()).getMaterial() == Material.WATER;
				if(isWater) {
					EntityBoat boat = new EntityLurkerSkinRaft(worldIn, rayTrace.hitVec.x, rayTrace.hitVec.y - 0.12D, rayTrace.hitVec.z, stack);
					boat.rotationYaw = playerIn.rotationYaw;

					if(worldIn.getCollisionBoxes(boat, boat.getEntityBoundingBox().grow(-0.1D)).isEmpty()) {
						if(!worldIn.isRemote) {
							worldIn.spawnEntity(boat);

							if(!playerIn.isSneaking()) {
								playerIn.startRiding(boat);
							}
						}

						if(!playerIn.capabilities.isCreativeMode) {
							stack.shrink(1);
						}

						playerIn.addStat(StatList.getObjectUseStats(this));

						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
					}
				}
			}
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
