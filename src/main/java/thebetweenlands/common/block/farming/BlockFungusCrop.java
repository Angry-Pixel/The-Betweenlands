package thebetweenlands.common.block.farming;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
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
				worldIn.spawnEntity(sporeling);
				worldIn.setBlockToAir(pos);
				this.harvestAndUpdateSoil(worldIn, pos, 5);

				for (EntityPlayerMP playerMP : worldIn.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(pos, pos).grow(10.0D, 5.0D, 10.0D))) {
					AdvancementCriterionRegistry.SPORELING_HATCH.trigger(playerMP);
                }
			}
		}
	}

	@Override
	protected float getGrowthChance(World world, BlockPos pos, IBlockState state, Random rand) {
		return 0.9F;
	}

	@Override
	public int getCropDrops(IBlockAccess world, BlockPos pos, Random rand, int fortune) {
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 1 + (fortune > 0 ? rand.nextInt(1 + fortune) : 0);
		}
		return 0;
	}

	@Override
	public ItemStack getSeedDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.SPORES);	
	}

	@Override
	public ItemStack getCropDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return this.isDecayed(world, pos) ? ItemStack.EMPTY : new ItemStack(ItemRegistry.YELLOW_DOTTED_FUNGUS);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ItemRegistry.SPORES);
	}

	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
