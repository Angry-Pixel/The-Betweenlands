package thebetweenlands.common.block.farming;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockFungusCrop extends BlockGenericCrop implements ICustomItemBlock {
	public BlockFungusCrop() {
		this.setCreativeTab(null);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		if(this.isDecayed(worldIn, pos)) {
			if(!worldIn.isRemote && state.getValue(AGE) >= 15 && rand.nextInt(6) == 0) {
				EntitySporeling sporeling = new EntitySporeling(worldIn);
				sporeling.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
				worldIn.spawnEntityInWorld(sporeling);
				worldIn.setBlockToAir(pos);
				this.consumeCompost(worldIn, pos);
			}
		}
	}

	@Override
	public ItemStack getSeedDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.SPORES);	
	}

	@Override
	public ItemStack getCropDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return this.isDecayed(world, pos) ? null : new ItemStack(ItemRegistry.YELLOW_DOTTED_FUNGUS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public Item getRenderedItem() {
		return ItemRegistry.SPORES;
	}
}
