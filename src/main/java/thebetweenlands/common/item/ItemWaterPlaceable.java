package thebetweenlands.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemWaterPlaceable extends ItemBlock {
	public ItemWaterPlaceable(Block block) {
		super(block);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		RayTraceResult rayTrace = this.rayTrace(world, player, true);

		if(rayTrace == null) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		} else {
			if(rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos rayTracePos = rayTrace.getBlockPos();

				if(!world.isBlockModifiable(player, rayTracePos) || !player.canPlayerEdit(rayTracePos.offset(rayTrace.sideHit), rayTrace.sideHit, stack)) {
					return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
				}

				BlockPos placePos = rayTracePos.up();
				IBlockState iblockstate = world.getBlockState(rayTracePos);

				if(iblockstate.getMaterial() == Material.WATER && ((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0 && world.isAirBlock(placePos)) {
					net.minecraftforge.common.util.BlockSnapshot snapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, placePos);

					world.setBlockState(placePos, this.block.getDefaultState());

					if(net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(player, snapshot, net.minecraft.util.EnumFacing.UP, hand).isCanceled()) {
						snapshot.restore(true, false);
						return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
					}

					world.setBlockState(placePos, this.block.getDefaultState(), 11);

					if(player instanceof EntityPlayerMP) {
						CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, placePos, stack);
					}

					if(!player.capabilities.isCreativeMode) {
						stack.shrink(1);
					}

					player.addStat(StatList.getObjectUseStats(this));

					IBlockState state = world.getBlockState(placePos);
					SoundType soundtype = state.getBlock().getSoundType(state, world, placePos, player);
					world.playSound(player, rayTracePos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

					player.swingArm(hand);
					
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}
	}
}
