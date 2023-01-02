package thebetweenlands.common.block.plant;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.api.capability.ICustomStepSoundCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;
import thebetweenlands.common.entity.WeedWoodBushUncollidableEntity;
import thebetweenlands.common.entity.mobs.EntitySwarm;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BlockWeedwoodBush extends Block implements IShearable, ISickleHarvestable, ITintedBlock, IFarmablePlant {
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyIntegerUnlisted POS_X = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Y = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Z = new PropertyIntegerUnlisted("pos_z");

	public BlockWeedwoodBush() {
		super(Material.PLANTS);

		this.setHardness(0.35F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);

		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false))
				.withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false))
				.withProperty(UP, Boolean.valueOf(false))
				.withProperty(DOWN, Boolean.valueOf(false)));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {NORTH, EAST, WEST, SOUTH, UP, DOWN}, new IUnlistedProperty[]{POS_X, POS_Y, POS_Z});
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemMisc.WEEDWOOD_STICK.create(1));
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.ITEMS_MISC;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return EnumItemMisc.WEEDWOOD_STICK.getID();
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS || item.getItem() == ItemRegistry.SILT_CRAB_CLAW;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItem(hand);
		if (!world.isRemote && !held.isEmpty() && EnumItemMisc.PHEROMONE_THORAX_CLUTCH.isItemOf(held)) {
			world.setBlockState(pos, BlockRegistry.DEAD_WEEDWOOD_BUSH.getDefaultState());
			EntitySwarm swarm = new EntitySwarm(world);
			swarm.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			world.spawnEntity(swarm);
			held.damageItem(1, player);
			return true;
		}
		return true;
    }

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state
				.withProperty(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north(), EnumFacing.NORTH)))
				.withProperty(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east(), EnumFacing.EAST)))
				.withProperty(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south(), EnumFacing.SOUTH)))
				.withProperty(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west(), EnumFacing.WEST)))
				.withProperty(UP, Boolean.valueOf(this.canConnectTo(worldIn, pos.up(), EnumFacing.UP)))
				.withProperty(DOWN, Boolean.valueOf(this.canConnectTo(worldIn, pos.down(), EnumFacing.DOWN)));
	}

	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing dir) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block instanceof BlockWeedwoodBush && !(block instanceof BlockNesting);
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState)oldState;
		return state.withProperty(POS_X, pos.getX()).withProperty(POS_Y, pos.getY()).withProperty(POS_Z, pos.getZ());
	}

	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return Blocks.LEAVES.getRenderLayer();
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		if (entityIn instanceof EntityPlayer || entityIn instanceof WeedWoodBushUncollidableEntity) {
			return;
		}
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity){
		if(entity instanceof EntityPlayer) {
			entity.motionX *= 0.06D;
			entity.motionZ *= 0.06D;
		}
		
		if(entity.world.isRemote) {
			ICustomStepSoundCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_CUSTOM_STEP_SOUND, null);
			if(cap != null) {
				if(entity.distanceWalkedOnStepModified > cap.getNextWeedwoodBushStep()) {
					AxisAlignedBB aabb = entity.getEntityBoundingBox();
					Iterator<MutableBlockPos> it = BlockPos.getAllInBoxMutable(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)).iterator();
					while(it.hasNext()) {
						MutableBlockPos checkPos = it.next();
						if(entity.world.getBlockState(checkPos).getBlock() == BlockRegistry.WEEDWOOD_BUSH) {
							spawnLeafParticles(entity.world, checkPos, Math.min((entity.distanceWalkedOnStepModified - cap.getNextWeedwoodBushStep()) * 40, 1));
						}
					}
					
					entity.world.playSound(entity.posX, entity.posY, entity.posZ, SoundRegistry.GECKO_HIDE, SoundCategory.BLOCKS, 0.4F, entity.world.rand.nextFloat() * 0.3F + 0.7F, false);
					
					cap.setNextWeeedwoodBushStep(entity.distanceWalkedOnStepModified + 0.8F);
				}
			}
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if(!worldIn.isRemote && !stack.isEmpty() && stack.getItem() instanceof ItemShears) {
			player.addStat(StatList.getBlockStats(this));
			player.addExhaustion(0.025F);
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(BlockRegistry.WEEDWOOD_BUSH);
	}
	
	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}

	@SideOnly(Side.CLIENT)
	protected static void spawnLeafParticles(World world, BlockPos pos, float strength) {
		int leafCount = (int)(60 * strength) + 1;
		float x = pos.getX() + 0.5F;
		float y = pos.getY() + 0.5F;
		float z = pos.getZ() + 0.5F;
		while (leafCount-- > 0) {
			float dx = world.rand.nextFloat() * 2 - 1;
			float dy = world.rand.nextFloat() * 2 - 0.5F;
			float dz = world.rand.nextFloat() * 2 - 1;
			float mag = 0.01F + world.rand.nextFloat() * 0.07F;
			BLParticles.WEEDWOOD_LEAF.spawn(world, x, y, z, ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
		}
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		return world.isAirBlock(targetPos) && this.canPlaceBlockAt(world, targetPos);
	}
	
	@Override
	public float getSpreadChance(World world, BlockPos pos, IBlockState state, BlockPos taretPos, Random rand) {
		return 0.35F;
	}

	@Override
	public void spreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.getDefaultState());
	}

	@Override
	public void decayPlant(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockToAir(pos);
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, IBlockState state, Random rand) {
		return 2;
	}
}
