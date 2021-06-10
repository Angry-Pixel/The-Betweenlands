package thebetweenlands.common.item.misc;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.terrain.BlockFallenLeaves;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemFallenLeaves extends ItemBlock {
	public ItemFallenLeaves(Block block) {
		super(block);
		this.setMaxDamage(0);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if(!stack.isEmpty() && player.canPlayerEdit(pos, facing, stack)) {
			IBlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			BlockPos offsetPos = pos;

			if((facing != EnumFacing.UP || block != this.block) && !block.isReplaceable(worldIn, pos)) {
				offsetPos = pos.offset(facing);
				state = worldIn.getBlockState(offsetPos);
				block = state.getBlock();
			}

			if(block == this.block) {
				int layers = state.getValue(BlockFallenLeaves.LAYERS);

				if(layers < 4) {
					IBlockState newState = state.withProperty(BlockFallenLeaves.LAYERS, layers + 1);
					AxisAlignedBB collisionBox = newState.getCollisionBoundingBox(worldIn, offsetPos);
					
					if((collisionBox == Block.NULL_AABB || (collisionBox != null && worldIn.checkNoEntityCollision(collisionBox.offset(offsetPos)))) && worldIn.setBlockState(offsetPos, newState, 10)) {
						SoundType placeSound = this.block.getSoundType(newState, worldIn, pos, player);
						worldIn.playSound(player, offsetPos, placeSound.getPlaceSound(), SoundCategory.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);

						if(player instanceof EntityPlayerMP) {
							CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
						}

						stack.shrink(1);
						return EnumActionResult.SUCCESS;
					}
				}
			}

			return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		} else {
			return EnumActionResult.FAIL;
		}
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		IBlockState state = world.getBlockState(pos);
		return (state.getBlock() != BlockRegistry.FALLEN_LEAVES || state.getValue(BlockFallenLeaves.LAYERS) >= 4) ? super.canPlaceBlockOnSide(world, pos, side, player, stack) : true;
	}
}