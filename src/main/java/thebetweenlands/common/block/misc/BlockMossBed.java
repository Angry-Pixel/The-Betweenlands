package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMossBed;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockMossBed extends BlockBed implements IStateMappedBlock, ICustomItemBlock {
	public BlockMossBed() { 
		this.setCreativeTab(null);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(0.2F);
		this.disableStats();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? Items.AIR : ItemRegistry.MOSS_BED_ITEM;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ItemRegistry.MOSS_BED_ITEM);
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.MOSS_BED_ITEM));
        }
    }

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
		if (state.getValue(PART) == BlockBed.EnumPartType.HEAD && te instanceof TileEntityMossBed) {
			spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.MOSS_BED_ITEM));
		} else {
			super.harvestBlock(worldIn, player, pos, state, null, stack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.ignore(OCCUPIED);
	}

	@Override
	public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, Entity player) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			if(!worldIn.isRemote) {
				playerIn.setSpawnPoint(pos, false);
				playerIn.sendStatusMessage(new TextComponentTranslation("chat.bed_spawn_set"), true);
			}
			return true;
		} else {
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMossBed();
	}
}