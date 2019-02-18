package thebetweenlands.common.block.plant;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.api.capability.ICustomStepSoundCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.property.IntegerPropertyUnlisted;
import thebetweenlands.common.entity.WeedWoodBushUncollidableEntity;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BlockWeedwoodBush extends Block implements IShearable, ISickleHarvestable, ITintedBlock, IFarmablePlant {
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final IntegerPropertyUnlisted POS_X = new IntegerPropertyUnlisted("pos_x");
	public static final IntegerPropertyUnlisted POS_Y = new IntegerPropertyUnlisted("pos_x");
	public static final IntegerPropertyUnlisted POS_Z = new IntegerPropertyUnlisted("pos_z");

	public BlockWeedwoodBush() {
		super(Material.PLANTS);

		this.setHardness(0.35F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);

		this.setDefaultState(this.blockState.getBaseState()
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(UP, Boolean.valueOf(false))
				.with(DOWN, Boolean.valueOf(false)));
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
	public boolean isHarvestable(ItemStack item, IWorldReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemMisc.WEEDWOOD_STICK.create(1));
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.ITEMS_MISC;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return EnumItemMisc.WEEDWOOD_STICK.getID();
	}

	@Override
	public boolean isShearable(ItemStack item, IWorldReader world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public IBlockState getActualState(IBlockState state, IWorldReader worldIn, BlockPos pos) {
		return state
				.with(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north())))
				.with(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east())))
				.with(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south())))
				.with(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west())))
				.with(UP, Boolean.valueOf(this.canConnectTo(worldIn, pos.up())))
				.with(DOWN, Boolean.valueOf(this.canConnectTo(worldIn, pos.down())));
	}

	public boolean canConnectTo(IWorldReader worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block == this;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IWorldReader worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState)oldState;
		return state.with(POS_X, pos.getX()).with(POS_Y, pos.getY()).with(POS_Z, pos.getZ());
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
	public void onEntityCollision(IBlockState state, World world, BlockPos pos, Entity entity){
		if(entity instanceof EntityPlayer) {
			entity.motionX *= 0.06D;
			entity.motionZ *= 0.06D;
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if(!worldIn.isRemote() && !stack.isEmpty() && stack.getItem() instanceof ItemShears) {
			player.addStat(StatList.getBlockStats(this));
			player.addExhaustion(0.025F);
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(BlockRegistry.WEEDWOOD_BUSH);
	}
	
	@Override
	public int getColorMultiplier(IBlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getFoliageColor(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}

	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		
		entity.getCapability(CapabilityRegistry.CAPABILITY_CUSTOM_STEP_SOUND).ifPresent(cap -> {
			if(entity.distanceWalkedOnStepModified > cap.getNextWeedwoodBushStep()) {
				boolean inBush = false;
				AxisAlignedBB aabb = entity.getBoundingBox();
				Iterator<MutableBlockPos> it = BlockPos.getAllInBoxMutable(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ)).iterator();
				while(it.hasNext()) {
					MutableBlockPos pos = it.next();
					if(entity.world.isBlockLoaded(pos) && entity.world.getBlockState(pos).getBlock() == BlockRegistry.WEEDWOOD_BUSH) {
						inBush = true;
						if(entity.world.isRemote()) {
							spawnLeafParticles(entity.world, pos, Math.min((entity.distanceWalkedOnStepModified - cap.getNextWeedwoodBushStep()) * 40, 1));
						}
					}
				}
				if(inBush) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundRegistry.GECKO_HIDE, SoundCategory.BLOCKS, 0.4F, entity.world.rand.nextFloat() * 0.3F + 0.7F);
				}
				cap.setNextWeeedwoodBushStep(entity.distanceWalkedOnStepModified + 0.8F);
			}	
		});
	}

	@OnlyIn(Dist.CLIENT)
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
		return rand.nextFloat() <= 0.35F && world.isAirBlock(targetPos) && this.canPlaceBlockAt(world, targetPos);
	}

	@Override
	public void spreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.getDefaultState());
	}

	@Override
	public void decayPlant(World world, BlockPos pos, IBlockState state, Random rand) {
		world.removeBlock(pos);
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, IBlockState state, Random rand) {
		return 2;
	}
}
