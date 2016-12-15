package thebetweenlands.common.block.terrain;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.IEntityBL;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.registries.ItemRegistry;


public class BlockMud extends Block {
	public BlockMud() {
		super(BLMaterialRegistry.MUD);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	public boolean canEntityWalkOnMud(Entity entity) {
		//TODO: REIMPLEMENT WHEN POTIONS ARE READDED
		//if(entity instanceof EntityLivingBase && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive((EntityLivingBase)entity)) return false;
		boolean canWalk = entity instanceof EntityPlayer && ((EntityPlayer)entity).inventory.armorInventory[0] != null && ((EntityPlayer)entity).inventory.armorInventory[0].getItem() instanceof ItemRubberBoots;
		boolean hasLurkerArmor = entity instanceof EntityPlayer && entity.isInWater() && ((EntityPlayer) entity).inventory.armorInventory[0] != null && ((EntityPlayer) entity).inventory.armorInventory[0].getItem() == ItemRegistry.LURKER_SKIN_BOOTS;
		return entity.isInWater() || entity instanceof IEntityBL || entity instanceof EntityItem || canWalk || hasLurkerArmor || (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode && ((EntityPlayer)entity).capabilities.isFlying);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> aabblist, @Nullable Entity entity) {
		AxisAlignedBB blockAABB = this.getCollisionBoundingBox(state, world, pos).offset(pos);
		if (blockAABB != null && aabb.intersectsWith(blockAABB) && (entity == null || this.canEntityWalkOnMud(entity))) {
			aabblist.add(blockAABB);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity){
		if (!this.canEntityWalkOnMud(entity)) {
			entity.motionX *= 0.08D;
			if(!entity.isInWater() && entity.motionY < 0 && entity.onGround) entity.motionY = -0.1D;
			entity.motionZ *= 0.08D;
			if(!entity.isInWater()) {
				entity.setInWeb();
			} else {
				entity.motionY *= 0.02D;
			}
			entity.onGround = true;
			if(entity instanceof EntityLivingBase && entity.isInsideOfMaterial(BLMaterialRegistry.MUD)) {
				entity.attackEntityFrom(DamageSource.inWall, 2.0F);
			}
		}
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState blockState) {
		return true;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}


	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return true;
	}
}