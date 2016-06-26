package thebetweenlands.common.block.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.entity.mobs.IEntityBL;
import thebetweenlands.common.item.armor.ItemRubberBoots;

public class BlockSilt extends BasicBlock {
	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1 - 0.125F, 1);

	public BlockSilt() {
		super(Material.SAND);
		setHardness(0.5F);
		setSoundType(SoundType.SAND);
		setHarvestLevel("shovel", 0);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return BOUNDING_BOX;
	}

	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		boolean canWalk = entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).inventory.armorInventory[0] != null 
				&& ((EntityPlayer)entityIn).inventory.armorInventory[0].getItem() instanceof ItemRubberBoots;
		if(!(entityIn instanceof IEntityBL) && !canWalk) {
			entityIn.motionX *= 0.4D;
			entityIn.motionZ *= 0.4D;
		}
	}
}
