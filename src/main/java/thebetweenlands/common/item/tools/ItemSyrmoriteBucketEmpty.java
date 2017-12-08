package thebetweenlands.common.item.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.world.World;
import net.minecraftforge.fluids.UniversalBucket;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.block.misc.BlockRubberTap;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemSyrmoriteBucketEmpty extends ItemBLBucketEmpty {
	@Override
	protected UniversalBucket getFilledBucket() {
		return ItemRegistry.SYRMORITE_BUCKET_FILLED;
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
								&& BlockRegistry.SYRMORITE_RUBBER_TAP.canPlaceBlockAt(world, pos.offset(result.sideHit))) {
							world.setBlockState(pos.offset(result.sideHit), BlockRegistry.SYRMORITE_RUBBER_TAP.getDefaultState().withProperty(BlockRubberTap.FACING, result.sideHit));
							itemStack.shrink(1);

							world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.PLAYERS, 1, 1);

							return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
						}
					}
				}
			}
		}

		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.syrmorite_bucket"), 0));
	}
}
