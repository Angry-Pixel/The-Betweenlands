package thebetweenlands.common.block.structure;

import java.util.Objects;
import java.util.Random;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;

public class BlockMobSpawnerBetweenlands extends BlockMobSpawner {
	public static String[] entityList = new String[]{"thebetweenlands.swamp_hag", "thebetweenlands.wight", "thebetweenlands.blood_snail", "thebetweenlands.termite", "thebetweenlands.leech"};

	public BlockMobSpawnerBetweenlands(){
		super();
		setHardness(10.0F);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}


	public static void setMob(World world, BlockPos pos, String mobName) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityMobSpawnerBetweenlands) {
			MobSpawnerLogicBetweenlands spawnerLogic = ((TileEntityMobSpawnerBetweenlands) te).getSpawnerLogic();
			String prevMob = spawnerLogic.getEntityNameToSpawn();
			spawnerLogic.setNextEntityName(mobName);
			//resets the rendered entity
			if (world.isRemote && !Objects.equals(prevMob, mobName)) {
				NBTTagCompound nbt = new NBTTagCompound();
				spawnerLogic.writeToNBT(nbt);
				spawnerLogic.readFromNBT(nbt);
			}
		}
	}


	public static void setRandomMob(World world, BlockPos pos, Random random) {
		setMob(world, pos, entityList[random.nextInt(entityList.length)]);
	}

	public static MobSpawnerLogicBetweenlands getLogic(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityMobSpawnerBetweenlands)
			return ((TileEntityMobSpawnerBetweenlands) te).getSpawnerLogic();
		return null;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		Random random = new Random();
		setRandomMob(worldIn, pos, random);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMobSpawnerBetweenlands();
	}


	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}


}
