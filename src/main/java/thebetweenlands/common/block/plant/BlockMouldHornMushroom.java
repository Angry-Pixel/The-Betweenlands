package thebetweenlands.common.block.plant;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.world.storage.location.LocationSporeHive;

public class BlockMouldHornMushroom extends Block implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {
	public static final PropertyEnum<EnumMouldHorn> MOULD_HORN_TYPE = PropertyEnum.<EnumMouldHorn>create("type", EnumMouldHorn.class);

	public BlockMouldHornMushroom() {
		super(Material.WOOD);
		setSoundType(SoundType.CLOTH);
		setTickRandomly(true);
		setHardness(0.2F);
		setDefaultState(blockState.getBaseState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM));
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos) == this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_MYCELIUM.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_STALK_THIN.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_CAP_THIN.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_STALK_FULL.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_CAP_FULL.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_CAP_FULL_WARTS.ordinal()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		float widthMin = 0, heightMin = 0, depthMin = 0;
		float widthMax = 0, heightMax = 0, depthMax = 0;
		switch (state.getValue(MOULD_HORN_TYPE)) {
		case MOULD_HORN_MYCELIUM:
			widthMin = 0F;
			heightMin = 0F;
			depthMin = 0F;
			widthMax = 1F;
			heightMax = 0.5F;
			depthMax = 1F;
			break;
		case MOULD_HORN_STALK_THIN:
		case MOULD_HORN_CAP_THIN:
			widthMin = 0.25F;
			heightMin = 0F;
			depthMin = 0.25F;
			widthMax = 0.75F;
			heightMax = 1F;
			depthMax = 0.75F;
			break;
		case MOULD_HORN_STALK_FULL:
		case MOULD_HORN_CAP_FULL:
		case MOULD_HORN_CAP_FULL_WARTS:
			widthMin = 0F;
			heightMin = 0F;
			depthMin = 0F;
			widthMax = 1F;
			heightMax = 1F;
			depthMax = 1F;
			break;
		}
		return new AxisAlignedBB(widthMin, heightMin, depthMin, widthMax, heightMax, depthMax);
	}
	
	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.byMetadata(meta));
    }

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(new IProperty[] { MOULD_HORN_TYPE }).build();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.byMetadata(meta));
	}

	private boolean checkForAdjacentBlockingMushroom(World world, BlockPos pos, int offSetXIn, int offSetZIn, IBlockState state) {
		for(int offsetX = -offSetXIn; offsetX <= offSetXIn; offsetX++)
			for(int offsetZ = -offSetZIn; offsetZ <= offSetZIn; offsetZ++)
				if (isBlockingMushroomAdjacent(world.getBlockState(pos.add(offsetX, 0, offsetZ)), state))
					return true;
		return false;
	}

	private boolean isBlockingMushroomAdjacent(IBlockState posState, IBlockState stateChecked) {
        return posState.getBlock() instanceof BlockMouldHornMushroom && posState.getValue(MOULD_HORN_TYPE) == stateChecked.getValue(MOULD_HORN_TYPE);
	}

	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
	/*	if (world.isRemote)
			return;
			if (!checkForAdjacentBigBlocks(world, pos, state)) {
				randomTick(world, pos, state, random);
				world.scheduleUpdate(pos, this, 100);
		}*/
	}

	@Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (world.isRemote)
			return;
		
		// Don't load chunks
		for(EnumFacing dir : EnumFacing.HORIZONTALS) {
			if(!world.isBlockLoaded(pos.offset(dir, 3))) {
				return;
			}
		}
		
		LocationSporeHive hive = LocationSporeHive.getAtBlock(world, pos);
		if(hive != null && !hive.isGrowing()) {
			// Don't grow if hive stopped growing
			return;
		}
		
		//if(world.rand.nextInt(4) == 0) {
			EnumMouldHorn stage = state.getValue(MOULD_HORN_TYPE);
			IBlockState basicBitch = BlockRegistry.MOULD_HORN.getDefaultState();
	        IBlockState mycelium = basicBitch.withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM);
	        IBlockState smallStalk = basicBitch.withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_THIN);
	        IBlockState smallCap = basicBitch.withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_THIN);
	        IBlockState fullStalk = basicBitch.withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_FULL);
	        IBlockState fullCap = basicBitch.withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_FULL);
	        IBlockState warts = basicBitch.withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_FULL_WARTS);
	
	        // BASIC GROWTH TICK TESTING - can make more varied later
			switch (stage) {
			case MOULD_HORN_MYCELIUM:
				if(checkForAdjacentBlockingMushroom(world, pos, 1, 1, fullStalk) || checkForAdjacentBlockingMushroom(world, pos, 1, 1, fullCap))
					break;
				else if(checkForAdjacentBlockingMushroom(world, pos, 1, 1, smallStalk) || checkForAdjacentBlockingMushroom(world, pos, 1, 1, smallCap))
					break;
				else
					LocationSporeHive.overgrowBlock(world, pos, smallCap, 2);
				break;
			case MOULD_HORN_CAP_THIN:
				if(world.getBlockState(pos.down()) != smallStalk && world.isAirBlock(pos.up())) {
					LocationSporeHive.overgrowBlock(world, pos, smallStalk, 3);
					LocationSporeHive.overgrowBlock(world, pos.up(), smallCap, 2);
					break;
				}
				if(checkForAdjacentBlockingMushroom(world, pos, 2, 2, fullStalk) || checkForAdjacentBlockingMushroom(world, pos, 2, 2, fullCap))
					break;
				else
					if(world.getBlockState(pos.down()) == smallStalk)
						LocationSporeHive.overgrowBlock(world, pos, fullCap, 2);
				break;
			case MOULD_HORN_STALK_THIN:
				if(checkForAdjacentBlockingMushroom(world, pos, 1, 1, fullStalk) || checkForAdjacentBlockingMushroom(world, pos, 1, 1, fullCap))
					break;
				else
					if(world.getBlockState(pos.up()) == fullCap)
						LocationSporeHive.overgrowBlock(world, pos, fullStalk, 2);
				break;
			case MOULD_HORN_CAP_FULL: 
				if(world.getBlockState(pos.down()) == smallStalk) {
					LocationSporeHive.overgrowBlock(world, pos.down(), fullStalk, 2);
				} // test size atm
				else if(world.getBlockState(pos.down(3)) != fullStalk && world.isAirBlock(pos.up())) {
					LocationSporeHive.overgrowBlock(world, pos, fullStalk, 2);
					if(random.nextBoolean())
						LocationSporeHive.overgrowBlock(world, pos.up(), fullCap, 2);
					else
						LocationSporeHive.overgrowBlock(world, pos.up(), warts, 2);
				}
				break;
			default:
				break;
			}
		//}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.getValue(MOULD_HORN_TYPE) == EnumMouldHorn.MOULD_HORN_STALK_FULL) {
			Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
			
			if(renderView != null && renderView.getDistanceSqToCenter(pos) < 100 && worldIn.rand.nextInt(200) == 0 && worldIn.getBlockState(pos.down()).getBlock() == BlockRegistry.MOULDY_SOIL) {
				BLParticles.MOULD_THROBBING.spawn(worldIn, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D);
			}
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumMouldHorn)state.getValue(MOULD_HORN_TYPE)).getMetadata();
	}

	public static enum EnumMouldHorn implements IStringSerializable {
		MOULD_HORN_MYCELIUM,
		MOULD_HORN_STALK_THIN,
		MOULD_HORN_CAP_THIN,
		MOULD_HORN_STALK_FULL,
		MOULD_HORN_CAP_FULL,
		MOULD_HORN_CAP_FULL_WARTS;

		private final String name;

		private EnumMouldHorn() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumMouldHorn byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumMouldHorn.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumMouldHorn.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumMouldHorn.values()[meta].getName();
	}
}
