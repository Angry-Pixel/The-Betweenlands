package thebetweenlands.common.item.farming;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class ItemPlantTonic extends Item {
	protected final ItemStack empty;

	public ItemPlantTonic(ItemStack empty) {
		this.empty = empty;
		this.setCreativeTab(BLCreativeTabs.GEARS);
		this.setMaxStackSize(1);
		this.setMaxDamage(3);
	}

	@Override
	public EnumActionResult onItemUse( EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() instanceof IPlantable || state.getBlock() instanceof IFarmablePlant) {
			while(world.getBlockState(pos).getBlock() instanceof IPlantable || world.getBlockState(pos).getBlock() instanceof IFarmablePlant) {
				pos = pos.down();
			}
		}

		state = world.getBlockState(pos);

		if(state.getBlock() instanceof BlockGenericDugSoil && BlockGenericDugSoil.getTile(world, pos) != null) {
			boolean cured = false;

			for(int xo = -2; xo <= 2; xo++) {
				for(int yo = -2; yo <= 2; yo++) {
					for(int zo = -2; zo <= 2; zo++) {
						BlockPos offsetPos = pos.add(xo, yo, zo);
						TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, offsetPos);
						if(te != null && te.getDecay() > 0) {
							cured = true;
							if(!world.isRemote) {
								te.setDecay(0);
							} else {
								ItemDye.spawnBonemealParticles(world, offsetPos.up(), 6);
							}
						}
					}
				}
			}

			if(cured) {
				if(!world.isRemote && !player.isCreative()) {
					stack.setItemDamage(stack.getItemDamage() + 1);
					if(stack.getItemDamage() >= stack.getMaxDamage()) {
						player.setHeldItem(hand, !this.empty.isEmpty() ? this.empty.copy() : ItemStack.EMPTY);
					}
				}

				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1, 1);

				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}
}
