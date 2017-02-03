package thebetweenlands.common.item.farming;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.IFarmablePlant;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class ItemPlantTonic extends Item {
	protected final ItemStack empty;

	public ItemPlantTonic(ItemStack empty) {
		this.empty = empty;
		this.setCreativeTab(BLCreativeTabs.GEARS);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() instanceof BlockGenericCrop || state.getBlock() instanceof IFarmablePlant) {
			while(world.getBlockState(pos).getBlock() instanceof BlockGenericCrop || world.getBlockState(pos).getBlock() instanceof IFarmablePlant) {
				pos = pos.down();
			}
		}

		state = world.getBlockState(pos);

		if(state.getBlock() instanceof BlockGenericDugSoil && BlockGenericDugSoil.getTile(world, pos) != null && BlockGenericDugSoil.getTile(world, pos).getDecay() > 0){
			for(int xo = -1; xo <= 1; xo++) {
				for(int yo = -1; yo <= 1; yo++) {
					for(int zo = -1; zo <= 1; zo++) {
						BlockPos offsetPos = pos.add(xo, yo, zo);
						TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, offsetPos);
						if(te != null) {
							if(!world.isRemote) {
								te.setDecay(0);
							} else {
								ItemDye.spawnBonemealParticles(world, offsetPos.up(), 6);
							}
						}
					}
				}
			}

			if(!world.isRemote && !player.isCreative()) {
				player.setHeldItem(hand, this.empty != null ? this.empty.copy() : null);
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}
}
