package thebetweenlands.common.block.plant;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.IDungeonFogBlock;
import thebetweenlands.client.handler.ItemTooltipHandler;

public class BlockSludgeDungeonHangingPlant extends BlockHangingPlant {
	@Override
	protected boolean canGrowAt(World world, BlockPos pos, IBlockState state) {
		if(super.canGrowAt(world, pos, state)) {
			for(BlockPos.MutableBlockPos checkPos : BlockPos.getAllInBoxMutable(pos.add(-6, -4, -6), pos.add(6, 0, 6))) {
				if(world.isBlockLoaded(checkPos)) {
					IBlockState offsetState = world.getBlockState(checkPos);
					Block offsetBlock = offsetState.getBlock();
					if(offsetBlock instanceof IDungeonFogBlock && ((IDungeonFogBlock) offsetBlock).isCreatingDungeonFog(world, checkPos, offsetState)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.sludge_dungeon_plant.mist"), 0));
	}
}
