package thebetweenlands.common.item.farming;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.block.farming.BlockAspectrusCrop;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemAspectrusSeeds extends ItemPlantableSeeds {
	public ItemAspectrusSeeds() {
		super(() -> BlockRegistry.ASPECTRUS_CROP.getDefaultState());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		IBlockState state = worldIn.getBlockState(pos);
		if (facing == EnumFacing.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, stack) && 
				state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) 
				&& worldIn.getBlockState(pos.up()).getBlock() == BlockRegistry.RUBBER_TREE_PLANK_FENCE
				&& (this.soilMatcher == null || this.soilMatcher.test(state))) {
			IBlockState plantState = this.crops.get();
			worldIn.setBlockState(pos.up(), plantState);
			this.onPlant(playerIn, worldIn, pos.up(), hand, facing, hitX, hitY, hitZ, plantState);
			stack.shrink(1);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}

	@Override
	protected void onPlant(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState state) {
		ItemStack stack = player.getHeldItem(hand);
		List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();
		if(!itemAspects.isEmpty()) {
			((BlockAspectrusCrop)state.getBlock()).setAspect(world, pos, itemAspects.get(0));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();
		if(!itemAspects.isEmpty()) {
			Aspect aspect = itemAspects.get(0);
			return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".filled.name", aspect.type.getName(), aspect.getRoundedDisplayAmount()).trim();
		}
		return super.getItemStackDisplayName(stack);
	}
}
