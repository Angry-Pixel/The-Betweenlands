package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IMudWalkerCapability;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;


public class BlockMud extends Block {
	protected static final AxisAlignedBB MUD_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);
	public static final PropertyBool IN_WATER = PropertyBool.create("in_water");
	
	public BlockMud() {
		super(BLMaterialRegistry.MUD);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setLightOpacity(255);
		setDefaultState(this.blockState.getBaseState().withProperty(IN_WATER, false));
	}

	public boolean canEntityWalkOnMud(Entity entity) {
		if (entity instanceof EntityLivingBase && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive((EntityLivingBase) entity))
			return false;
		boolean canWalk = entity instanceof EntityPlayer && ItemRubberBoots.canEntityWalkOnMud(entity);
		boolean hasLurkerArmor = entity instanceof EntityPlayer && entity.isInWater() && !((EntityPlayer) entity).inventory.armorInventory.isEmpty() && ((EntityPlayer) entity).inventory.armorInventory.get(0).getItem() == ItemRegistry.LURKER_SKIN_BOOTS;
		IMudWalkerCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_MUD_WALKER, null);
		boolean mudWalker = entity instanceof EntityPlayer && cap != null && cap.isActive();
		return entity instanceof IEntityBL || entity instanceof EntityItem || canWalk || hasLurkerArmor || mudWalker || (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode && ((EntityPlayer) entity).capabilities.isFlying);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return MUD_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
		AxisAlignedBB blockAABB = FULL_BLOCK_AABB.offset(pos);
		if (entityBox.intersects(blockAABB) && (entity == null || canEntityWalkOnMud(entity)))
			collidingBoxes.add(blockAABB);
		else if (world.isRemote) {
			blockAABB = MUD_AABB.offset(pos);
			if (entityBox.intersects(blockAABB)) {
				collidingBoxes.add(blockAABB);
			}
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity){
		if (!canEntityWalkOnMud(entity)) {
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
				entity.attackEntityFrom(DamageSource.IN_WALL, 2.0F);
			}
		}
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		double d0 = (double) pos.getX();
		double d1 = (double) pos.getY();
		double d2 = (double) pos.getZ();

		if (rand.nextInt(10) == 0) {
			boolean stateBelow = world.isAirBlock(pos.down());
			if (stateBelow) {
				double d3 = d0 + (double) rand.nextFloat();
				double d5 = d1 - 0.05D;
				double d7 = d2 + (double) rand.nextFloat();
				BLParticles.CAVE_WATER_DRIP.spawn(world, d3, d5, d7).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {IN_WATER});
    }

	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(IN_WATER, isUnderwater(worldIn, pos.up()));
    }

    public boolean isUnderwater(IBlockAccess worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        boolean inWater = iblockstate.getMaterial() == Material.WATER;
        return inWater;
    }
}