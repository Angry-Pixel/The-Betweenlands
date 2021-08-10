package thebetweenlands.common.block.container;

import java.util.List;
import java.util.Random;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.tile.TileEntityCrabPotFilter;
import thebetweenlands.util.AdvancedStateMap;

public class BlockCrabPotFilter extends BlockSwampWater implements ITileEntityProvider, IStateMappedBlock {
	
	public BlockCrabPotFilter() {
		this(FluidRegistry.SWAMP_WATER, Material.WATER);
		setHardness(2.0F);
	}

	public BlockCrabPotFilter(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setUnderwaterBlock(true);
		setDefaultState(blockState.getBaseState().withProperty(LEVEL, 0));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(LEVEL);
	}

	@Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.bl.crab_pot_filter_item"), 0));
        super.addInformation(stack, player, tooltip, advanced);
    }

	@Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntityCrabPotFilter) {
				TileEntityCrabPotFilter tile = (TileEntityCrabPotFilter) world.getTileEntity(pos);
				tile.setRotation(placer.getHorizontalFacing().rotateYCCW().getHorizontalIndex());
				tile.markForUpdate();
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCrabPotFilter();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_CRAB_POT_FILTER, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityCrabPotFilter tile = (TileEntityCrabPotFilter) world.getTileEntity(pos);
		if (tile != null) {
			InventoryHelper.dropInventoryItems(world, pos, tile);
			world.removeTileEntity(pos);
		}
		super.breakBlock(world, pos, state);
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		TileEntityCrabPotFilter tile = (TileEntityCrabPotFilter) world.getTileEntity(pos);
		if (tile != null) {
			if (rand.nextInt(3) == 0)
				if (tile.active && tile.getSlotProgress() > 0) {
					for (int i = 0; i < 5 + rand.nextInt(5); i++) {
						BatchedParticleRenderer.INSTANCE.addParticle(
								DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR,
								BLParticles.SMOOTH_SMOKE.create(world, pos.getX() + 0.5F, pos.getY() + 0.99F, pos.getZ() + 0.5F,
										ParticleArgs.get().withMotion((rand.nextFloat() - 0.5f) * 0.01f, -rand.nextFloat() * -0.05F - 0.05F, (rand.nextFloat() - 0.5f) * 0.01f)
												.withScale(0.5f + rand.nextFloat() * 8.0F)
												.withColor(0.59F, 0.29F, 0F, 0.3f).withData(80, true, 0.01F, true)));
					}
				}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

	@Override
	public ItemBlock getItemBlock() {
		return ICustomItemBlock.getDefaultItemBlock(this);
	}

}
