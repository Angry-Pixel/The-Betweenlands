package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityTarBarrel;

public class ItemTarBarrel extends ItemBlock {
	private static final String NBT_FLUID_STACK = "bl.fluidStack";

	public ItemTarBarrel(Block block) {
		super(block);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stack.getTagCompound() != null ? 1 : super.getItemStackLimit(stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		FluidStack fluidStack = this.getFluidStack(stack);
		if(fluidStack != null) {
			tooltip.add(fluidStack.getLocalizedName() + " (" + fluidStack.amount + "mb)");
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if(super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
			FluidStack fluidStack = this.getFluidStack(stack);
			if(fluidStack != null) {
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof TileEntityTarBarrel) {
					((TileEntityTarBarrel) te).fill(fluidStack, true);
				}
			}
			return true;
		}
		return false;
	}

	public FluidStack getFluidStack(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null && nbt.hasKey(NBT_FLUID_STACK, Constants.NBT.TAG_COMPOUND)) {
			return FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(NBT_FLUID_STACK));
		}
		return null;
	}

	public ItemStack fromBarrel(TileEntityTarBarrel te) {
		ItemStack stack = new ItemStack(this);

		IFluidTankProperties props = te.getTankProperties()[0];
		FluidStack fluidStack = props.getContents();

		if(fluidStack != null && fluidStack.amount > 0) {
			stack.setTagInfo(NBT_FLUID_STACK, fluidStack.writeToNBT(new NBTTagCompound()));
		}

		return stack;
	}
}
