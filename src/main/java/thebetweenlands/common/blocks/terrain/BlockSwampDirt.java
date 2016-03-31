package thebetweenlands.common.blocks.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thebetweenlands.client.tabs.BLCreativeTabs;
import thebetweenlands.common.blocks.BasicBlock;
import thebetweenlands.common.lib.ModInfo;

public class BlockSwampDirt extends BasicBlock {

	public BlockSwampDirt(Material materialIn) {
		super(materialIn);
		this.setStepSound(SoundType.GROUND);
		this.setHardness(0.5F);
		this.setCreativeTab(BLCreativeTabs.blocks);
		this.setUnlocalizedName(ModInfo.NAME_PREFIX + "swamp_dirt");
		this.setHarvestLevel("shovel", 0);
	}

	//TODO: Dirt right click
	/*@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (heldItem instanceof ItemSpadeBL) {
			if(!world.isRemote) {
				world.setBlock(pos, TheBetweenlands.REGISTRIES.blockRegistry.farmedDirt, 1, 3);
				world.playSound(player, pos, this.stepSound.getStepSound(), SoundCategory.BLOCKS, (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getPitch() * 0.8F);
				world.playAuxSFXAtEntity(null, 2001, pos, Block.getIdFromBlock(world.getBlock(x, y, z)));
				heldItem.damageItem(1, player);
			}
			return true;
		}
		return false;
	}*/
}
