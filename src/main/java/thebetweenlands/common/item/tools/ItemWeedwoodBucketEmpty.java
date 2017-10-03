package thebetweenlands.common.item.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.UniversalBucket;
import thebetweenlands.common.block.misc.BlockRubberTap;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemWeedwoodBucketEmpty extends ItemBLBucketEmpty {
	@Override
	protected UniversalBucket getFilledBucket() {
		return ItemRegistry.WEEDWOOD_BUCKET_FILLED;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		if(!world.isRemote) {
			RayTraceResult result = this.rayTrace(world, player, true);

			if(result != null && result.typeOfHit == Type.BLOCK && result.sideHit.getAxis() != Axis.Y) {
				BlockPos pos = result.getBlockPos();

				if(player.canPlayerEdit(pos, result.sideHit, itemStack)) {
					IBlockState blockState = world.getBlockState(pos);

					if(blockState.getBlock() == BlockRegistry.LOG_RUBBER && blockState.getValue(BlockRubberLog.NATURAL)) {
						if(world.getBlockState(pos.offset(result.sideHit)).getBlock().isReplaceable(world, pos.offset(result.sideHit))
								&& BlockRegistry.WEEDWOOD_RUBBER_TAP.canPlaceBlockAt(world, pos.offset(result.sideHit))) {
							if(player.inventory.hasItemStack(EnumItemMisc.SWAMP_REED_ROPE.create(1))) {
								world.setBlockState(pos.offset(result.sideHit), BlockRegistry.WEEDWOOD_RUBBER_TAP.getDefaultState().withProperty(BlockRubberTap.FACING, result.sideHit));
								itemStack.shrink(1);

								for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
									ItemStack invStack = player.inventory.getStackInSlot(i);
									if(!invStack.isEmpty() && EnumItemMisc.SWAMP_REED_ROPE.isItemOf(invStack)) {
										player.inventory.decrStackSize(i, 1);
										break;
									}
								}

								world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.PLAYERS, 1, 1);

								return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
							} else {
								player.sendMessage(new TextComponentTranslation("chat.tap.needsRope", new TextComponentTranslation(BlockRegistry.WEEDWOOD_RUBBER_TAP.getUnlocalizedName() + ".name")));
							}
						}
					}
				}
			}
		}

		return super.onItemRightClick(world, player, hand);
	}
}
