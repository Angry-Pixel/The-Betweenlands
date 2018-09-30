package thebetweenlands.common.item.farming;

import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import thebetweenlands.client.tab.BLCreativeTabs;


public class ItemPlantableSeeds extends ItemSeeds {
	protected final Supplier<IBlockState> crops;
	protected final Predicate<IBlockState> soilMatcher;

	public ItemPlantableSeeds(Supplier<IBlockState> crops) {
		this(crops, null);
	}

	public ItemPlantableSeeds(Supplier<IBlockState> crops, @Nullable Predicate<IBlockState> soilMatcher) {
		super(null, null);
		this.crops = crops;
		this.soilMatcher = soilMatcher;
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		IBlockState state = worldIn.getBlockState(pos);
		BlockBush bush = this.crops.get().getBlock() instanceof BlockBush ? (BlockBush) this.crops.get().getBlock() : null;
		BlockPos up = pos.up();
		if (facing == EnumFacing.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, stack) && 
				(bush == null ? state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) : bush.canSustainPlant(state, worldIn, pos, EnumFacing.UP, bush)) 
				&& worldIn.isAirBlock(up)
				&& (this.soilMatcher == null || this.soilMatcher.test(state))) {
			IBlockState plantState = this.crops.get();
			worldIn.setBlockState(up, plantState);
			if (playerIn instanceof EntityPlayerMP) {
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)playerIn, up, stack);
			}
			this.onPlant(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ, plantState);
			stack.shrink(1);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}

	protected void onPlant(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState state) {
		
	}
	
	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos){
		return EnumPlantType.Crop;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.crops.get();
	}
}
