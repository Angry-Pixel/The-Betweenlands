package thebetweenlands.common.block.terrain;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityTermite;

public class BlockRottenLog extends BlockLogBetweenlands {
	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		super.onBlockDestroyedByPlayer(world, pos, state);

		if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
			if (world.rand.nextInt(6) == 0) {
				EntityTermite entity = new EntityTermite(world);
				entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				if(!entity.isNotColliding()) {
					entity.setSmall(true);
				}
				world.spawnEntity(entity);
			}
		}
	}
	
	@Override
	public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
	}
}