package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.block.misc.BlockMistBridge;
import thebetweenlands.common.entity.EntityMistBridge;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemMistStaff extends Item {

	public ItemMistStaff() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {	
	ItemStack stack = player.getHeldItem(hand);
	BlockPos pos = player.getPosition().down();	
		if (!world.isRemote) {
			stack.damageItem(2, player);
			double direction = Math.toRadians(player.rotationYaw);
			Vec3d diag = new Vec3d(Math.sin(direction + Math.PI / 2.0D), 0, Math.cos(direction + Math.PI / 2.0D)).normalize();
			List<BlockPos> spawnedPos = new ArrayList<BlockPos>();
			for (int distance = -1; distance <= 16; distance++) {
				for(int distance2 = -distance; distance2 <= distance; distance2++) {
					for(int yo = 0; yo <= 1; yo++) {
						int originX = MathHelper.floor(pos.getX() + 0.5D - Math.sin(direction) * distance - diag.x * distance2 * 0.25D);
						int originY = pos.getY();// + yo;
						int originZ = MathHelper.floor(pos.getZ() + 0.5D + Math.cos(direction) * distance + diag.z * distance2 * 0.25D);
						BlockPos origin = new BlockPos(originX, originY, originZ);

						if(spawnedPos.contains(origin))
							continue;

						spawnedPos.add(origin);

						IBlockState block = world.getBlockState(new BlockPos(originX, originY, originZ));

						if ((block.isNormalCube() || block.getBlock().isReplaceable(world, origin)) && !block.getBlock().hasTileEntity(block) && block.getBlockHardness(world, origin) <= 5.0F && block.getBlockHardness(world, origin) >= 0.0F && !world.getBlockState(origin.up()).isOpaqueCube() && !(block.getBlock() instanceof BlockMistBridge)) {
							spawnEntity(world, origin, distance);
							break;
						}
					}
				}
			}
			world.playSound((EntityPlayer)null, pos, SoundRegistry.MIST_STAFF_CAST, SoundCategory.BLOCKS, 1F, 1.0F);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	private void spawnEntity(World world, BlockPos pos, int distance) {
		if (!world.isRemote) {// && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
			EntityMistBridge mist_bridge = new EntityMistBridge(world);
			mist_bridge.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			mist_bridge.setDelay(distance);
			if (mist_bridge.getCanSpawnHere()) {
				mist_bridge.onInitialSpawn(world.getDifficultyForLocation(mist_bridge.getPosition()), null);
				world.spawnEntity(mist_bridge);
			}
		}
	}

}
