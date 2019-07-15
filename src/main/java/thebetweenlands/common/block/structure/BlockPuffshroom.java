package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.structure.BlockMudTiles.EnumMudTileType;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityPuffshroom;

public class BlockPuffshroom extends Block implements ITileEntityProvider {
	public BlockPuffshroom() {
		super(Material.ROCK);
		setSoundType(SoundType.STONE);
		setHardness(8);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPuffshroom();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return this.tryHarvestWithShears(world, pos, state, player, player.getHeldItem(hand));
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		this.tryHarvestWithShears(world, pos, world.getBlockState(pos), player, player.getHeldItemMainhand());
	}
	
	protected boolean tryHarvestWithShears(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack stack) {
		if(!stack.isEmpty() && stack.getItem() instanceof ItemShears) {
			TileEntity tile = world.getTileEntity(pos);
			
			if(tile instanceof TileEntityPuffshroom) {
				TileEntityPuffshroom puffshroom = (TileEntityPuffshroom) tile;
				
				if(puffshroom.animation_1 >= 1) {
					if(!world.isRemote && world instanceof WorldServer) {
						LootTable lootTable = ((WorldServer) world).getLootTableManager().getLootTableFromLocation(LootTableRegistry.PUFFSHROOM);
						LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) world);
						
						List<ItemStack> loot = lootTable.generateLootForPools(((WorldServer) world).rand, lootBuilder.build());
						
						for(ItemStack lootStack : loot) {
							spawnAsEntity(world, pos.up(), lootStack);
						}
						
						world.setBlockState(pos, BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED), 3);
						
						world.playSound(null, pos, blockSoundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1F);
						world.playEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.MUD_FLOWER_POT));
						
						world.notifyBlockUpdate(pos, state, state, 3);
						
						stack.damageItem(1, player);
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
        return world.setBlockState(pos, BlockRegistry.MUD_TILES.getDefaultState().withProperty(BlockMudTiles.VARIANT, EnumMudTileType.MUD_TILES_CRACKED), world.isRemote ? 11 : 3);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return 0;
	}
}