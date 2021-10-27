package thebetweenlands.common.block.structure;

import java.util.Random;
import java.util.function.Consumer;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;

public class BlockMobSpawnerBetweenlands extends BlockMobSpawner {

	public static enum RandomSpawnerMob {
		SWAMP_HAG("thebetweenlands:swamp_hag", 200, 500, 4),
		WIGHT("thebetweenlands:wight", 400, 800, 2),
		BLOOD_SNAIL("thebetweenlands:blood_snail", 100, 400, 4),
		TERMITE("thebetweenlands:termite", 100, 300, 6),
		LEECH("thebetweenlands:leech", 150, 500, 3);

		private String name;
		private int minDelay, maxDelay, maxEntities;

		private RandomSpawnerMob(String name, int minDelay, int maxDelay, int maxEntities) {
			this.name = name;
			this.minDelay = minDelay;
			this.maxDelay = maxDelay;
			this.maxEntities = maxEntities;
		}

		public String getName() {
			return this.name;
		}

		public int getMinDelay() {
			return this.minDelay;
		}

		public int getMaxDelay() {
			return this.maxDelay;
		}

		public int getMaxEntities() {
			return this.maxEntities;
		}
	}

	public BlockMobSpawnerBetweenlands(){
		super();
		this.setSoundType(SoundType.GLASS);
		this.setHardness(10.0F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@SafeVarargs
	public static MobSpawnerLogicBetweenlands setMob(World world, BlockPos pos, String mobName, Consumer<MobSpawnerLogicBetweenlands>... consumers) {
		MobSpawnerLogicBetweenlands spawnerLogic = getLogic(world, pos);
		if(spawnerLogic != null) {
			spawnerLogic.setNextEntityName(mobName);
			for(Consumer<MobSpawnerLogicBetweenlands> c : consumers) {
				c.accept(spawnerLogic);
			}
		}
		return spawnerLogic;
	}

	@SafeVarargs
	public static MobSpawnerLogicBetweenlands setRandomMob(World world, BlockPos pos, Random random, Consumer<MobSpawnerLogicBetweenlands>... consumers) {
		RandomSpawnerMob mob = RandomSpawnerMob.values()[random.nextInt(RandomSpawnerMob.values().length)];
		MobSpawnerLogicBetweenlands logic = setMob(world, pos, mob.getName());
		if(logic != null) {
			logic.setDelayRange(mob.getMinDelay(), mob.getMaxDelay());
			logic.setMaxEntities(mob.getMaxEntities());
			for(Consumer<MobSpawnerLogicBetweenlands> c : consumers) {
				c.accept(logic);
			}
		}
		return logic;
	}

	public static MobSpawnerLogicBetweenlands getLogic(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityMobSpawnerBetweenlands) {
			return ((TileEntityMobSpawnerBetweenlands) te).getSpawnerLogic();
		}
		return null;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if(!worldIn.isRemote) {
			Random random = new Random();
			setRandomMob(worldIn, pos, random);
		}
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
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
