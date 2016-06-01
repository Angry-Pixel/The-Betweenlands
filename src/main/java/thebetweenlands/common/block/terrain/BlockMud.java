package thebetweenlands.common.block.terrain;

import java.util.List;


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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.IEntityBL;
import thebetweenlands.common.item.BLMaterial;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;


public class BlockMud extends Block {
    public BlockMud() {
        super(BLMaterial.MUD);
        setHardness(0.5F);
        setSoundType(SoundType.SAND);
        setHarvestLevel("shovel", 0);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        //setBlockName("thebetweenlands.mud");
        //setBlockTextureName("thebetweenlands:mud");
    }

    public boolean canEntityWalkOnMud(Entity entity) {
        //TODO: REIMPLEMENT WHEN POTIONS ARE READDED
        //if(entity instanceof EntityLivingBase && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive((EntityLivingBase)entity)) return false;
        boolean canWalk = entity instanceof EntityPlayer && ((EntityPlayer)entity).inventory.armorInventory[0] != null && ((EntityPlayer)entity).inventory.armorInventory[0].getItem() instanceof ItemRubberBoots;
        boolean hasLurkerArmor = entity instanceof EntityPlayer && entity.isInWater() && ((EntityPlayer)entity).inventory.armorInventory[0] != null && ((EntityPlayer)entity).inventory.armorInventory[0].getItem() == ItemRegistry.lurkerSkinBoots;
        return entity instanceof IEntityBL || entity instanceof EntityItem || canWalk || hasLurkerArmor || (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode && ((EntityPlayer)entity).capabilities.isFlying);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> aabblist, @Nullable Entity entity) {
        AxisAlignedBB blockAABB = this.getCollisionBoundingBox(state, world, pos);
        if (blockAABB != null && aabb.intersectsWith(blockAABB) && this.canEntityWalkOnMud(entity)) {
            if(entity instanceof IEntityBL || entity instanceof EntityItem) {
                aabblist.add(blockAABB);
            } else {
                if(world.isRemote) aabblist.add(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1, pos.getY()+1-0.125, pos.getZ()+1));
            }
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
            if(entity instanceof EntityLivingBase && entity.isInsideOfMaterial(BLMaterial.MUD)) {
                ((EntityLivingBase) entity).attackEntityFrom(DamageSource.inWall, 2.0F);
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
        return false;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        Block block = blockAccess.getBlockState(pos).getBlock();
        AxisAlignedBB axisalignedbb = state.getBoundingBox(blockAccess, pos);
        if(block == this) return false;
        switch (side)
        {
            case DOWN:
                if (axisalignedbb.minY > 0.0D) {return true;}
                break;

            case UP:
                if (axisalignedbb.maxY < 1.0D) {return true;}
                break;

            case NORTH:
                if (axisalignedbb.minZ > 0.0D) {return true;}
                break;

            case SOUTH:
                if (axisalignedbb.maxZ < 1.0D) {return true;}
                break;

            case WEST:
                if (axisalignedbb.minX > 0.0D) {return true;}
                break;

            case EAST:
                if (axisalignedbb.maxX < 1.0D) {return true;}
                break;
        }

        return !blockAccess.getBlockState(pos).getBlock().isOpaqueCube(state);
    }*/
}