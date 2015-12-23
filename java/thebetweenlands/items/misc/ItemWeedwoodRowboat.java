package thebetweenlands.items.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.utils.MathUtils;

public class ItemWeedwoodRowboat extends Item implements IManualEntryItem {
	public ItemWeedwoodRowboat() {
		maxStackSize = 1;
		setUnlocalizedName("thebetweenlands.weedwoodRowboat");
		setTextureName("thebetweenlands:weedwoodRowboat");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		float pitch = player.rotationPitch;
		float yaw = player.rotationYaw;
		double x = player.posX;
		double y = player.posY + 1.62 - player.yOffset;
		double z = player.posZ;
		Vec3 origin = Vec3.createVectorHelper(x, y, z);
		float cosYaw = MathHelper.cos(-yaw * MathUtils.DEG_TO_RAD - MathUtils.PI);
		float sinYaw = MathHelper.sin(-yaw * MathUtils.DEG_TO_RAD - MathUtils.PI);
		float cosPitch = -MathHelper.cos(-pitch * MathUtils.DEG_TO_RAD);
		float lookY = MathHelper.sin(-pitch * MathUtils.DEG_TO_RAD);
		float lookX = sinYaw * cosPitch;
		float lookZ = cosYaw * cosPitch;
		double reach = 5;
		Vec3 dest = origin.addVector(lookX * reach, lookY * reach, lookZ * reach);
		MovingObjectPosition raytrace = world.rayTraceBlocks(origin, dest, true);
		if (raytrace == null) {
			return itemStack;
		} else {
			Vec3 look = player.getLook(1);
			boolean intersecting = false;
			float expansion = 1;
			List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach).expand(expansion, expansion, expansion));
			for (Entity entity : entities) {
				if (entity.canBeCollidedWith()) {
					float size = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = entity.boundingBox.expand(size, size, size);
					if (aabb.isVecInside(origin)) {
						intersecting = true;
					}
				}
			}
			if (intersecting) {
				return itemStack;
			} else {
				if (raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					int blockX = raytrace.blockX;
					int blockY = raytrace.blockY;
					int blockZ = raytrace.blockZ;
					if (world.getBlock(blockX, blockY, blockZ) == Blocks.snow_layer) {
						blockY--;
					}
					EntityWeedwoodRowboat entityboat = new EntityWeedwoodRowboat(world, raytrace.hitVec.xCoord, raytrace.hitVec.yCoord - 1, raytrace.hitVec.zCoord);
					entityboat.rotationYaw = 270 + player.rotationYaw;
					if (!world.getCollidingBoundingBoxes(entityboat, entityboat.boundingBox.expand(-0.1, -0.1, -0.1)).isEmpty()) {
						return itemStack;
					}
					if (!world.isRemote) {
						world.spawnEntityInWorld(entityboat);
					}
					if (!player.capabilities.isCreativeMode) {
						itemStack.stackSize--;
					}
				}
				return itemStack;
			}
		}
	}

	@Override
	public String manualName(int meta) {
		return "weedwoodRowboat";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[] { 2 };
	}

	@Override
	public int metas() {
		return 0;
	}
}
