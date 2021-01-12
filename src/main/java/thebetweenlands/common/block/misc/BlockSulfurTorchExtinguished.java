package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSulfurTorchExtinguished extends BlockSulfurTorch {
	public BlockSulfurTorchExtinguished() {
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setSoundType(SoundType.WOOD);
		this.setLightLevel(0.0f);
	}

	@Override
	public void fillWithRain(World worldIn, BlockPos pos) { }

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack held = playerIn.getHeldItem(hand);
		if(!held.isEmpty() && held.getItem() == ItemRegistry.OCTINE_INGOT) {
			if(!worldIn.isRemote) {
				worldIn.setBlockState(pos, BlockRegistry.SULFUR_TORCH.getDefaultState().withProperty(FACING, state.getValue(FACING)));
				worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) { }
}
