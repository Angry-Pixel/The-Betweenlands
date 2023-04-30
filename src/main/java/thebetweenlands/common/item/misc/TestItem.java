package thebetweenlands.common.item.misc;

import java.util.UUID;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SporeHiveManager;
import thebetweenlands.common.world.storage.location.LocationSporeHive;


//MINE!!
public class TestItem extends Item {
	public TestItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!worldIn.isRemote) {
			LocalRegion region = LocalRegion.getFromBlockPos(playerIn.getPosition());
			System.out.println("Region: " + region.getFileName());
		}
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
			if (!worldIn.isRemote) {
				
		/*	IBlockState state = worldIn.getBlockState(pos);
			if (state.getBlock() instanceof BlockGenericDugSoil) {
				TileEntityDugSoil te = (TileEntityDugSoil) worldIn.getTileEntity(pos);
				te.setDecay(20);
			}
		/*	
		/*
			WorldGenTarPoolDungeon gen = new WorldGenTarPoolDungeon();
			gen.generate(worldIn, itemRand, pos.up());
		*/
		/*
			WorldGenDruidCircle worldGenDruidCircle = new WorldGenDruidCircle();
			worldGenDruidCircle.generateStructure(worldIn, itemRand, pos.up());
		*/
		/*
            WorldGenIdolHeads head = new WorldGenIdolHeads();
            head.generate(worldIn, itemRand, pos.up());
       */ 
		/*
            WorldGenSpawnerStructure smallRuins = new WorldGenSpawnerStructure();
            smallRuins.generate(worldIn, itemRand, pos.up());
		*/
		/*
			WorldGenWightFortress fortress = new WorldGenWightFortress();
			fortress.generate(worldIn, itemRand, pos.up());
		*/
		/*		
			WorldGenLakeCavernSimulacrum gen = new WorldGenLakeCavernSimulacrum();
			gen.generate(worldIn, itemRand, pos.up());
		*/
		/*
			WorldGenSmallRuins ruins = new WorldGenSmallRuins();
			ruins.generate(worldIn, itemRand, pos.up());
		*/
		/*
			if(player.isSneaking()) {
				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(worldIn);
				List<SharedStorage> storages = worldStorage.getSharedStorageAt(SharedStorage.class, (storage) -> {
					if(storage instanceof LocationStorage) {
						return ((LocationStorage)storage).isInside(pos);
					}
					return true;
				}, pos.getX(), pos.getZ());
				for(SharedStorage storage : storages) {
					worldStorage.removeSharedStorage(storage);
				}
			} else {
				WorldGenWightFortress fortress = new WorldGenWightFortress();
				fortress.generate(worldIn, itemRand, pos.up());
			}
		*/
		/*
			ItemAspectContainer container = ItemAspectContainer.fromItem(player.getHeldItem(hand));
			if(!player.isSneaking()) {
				container.add(AspectRegistry.AZUWYNN, 10);
				System.out.println("Added: 10");
			} else {
				System.out.println("Drained: " + container.drain(AspectRegistry.AZUWYNN, 8));
			}
		*/
		/*
			WorldGenSpawner spawner = new WorldGenSpawner();
			if(spawner.generate(worldIn, itemRand, pos)) {
				//playerIn.setHeldItem(hand, null);
			}
		*/
		/*
			WorldGenCragrockTower tower = new WorldGenCragrockTower();

			if(tower.generate(worldIn, itemRand, pos.up(8).add(8, 0, 0))) {
				//playerIn.setHeldItem(hand, null);
			}
		*/
		/*
			WorldGenSludgeWormDungeon dungeon = new WorldGenSludgeWormDungeon();
			//dungeon.makeMaze(worldIn, itemRand, pos.up().add(1, 0, 1));
			//dungeon.generateTower(worldIn, itemRand, pos.add(15, 0, 15));
			dungeon.generate(worldIn, itemRand, pos.up().add(1, 0, 1));
		*/

			//BlockGroundItem.create(worldIn, pos.up(), new ItemStack(ItemRegistry.ANCIENT_GREATSWORD));

		/*
			WorldGenNibbletwigTree tree = new WorldGenNibbletwigTree();
			if(tree.generate(worldIn, itemRand, pos.up(1))) {
				//playerIn.setHeldItem(hand, null);
			}
		*/
		/*
			WorldGenHearthgroveTree tree = new WorldGenHearthgroveTree();
			if(tree.generate(worldIn, itemRand, pos.up(1))) {
				//playerIn.setHeldItem(hand, null);
			}
		*/
		/*
			WorldGenSmallSpiritTree tree = new WorldGenSmallSpiritTree();
			if(tree.generate(worldIn, itemRand, pos.up(1))) {
				//playerIn.setHeldItem(hand, null);
			}
		*/
		/*
			WorldGenSpiritTreeStructure tree = new WorldGenSpiritTreeStructure();
			if(tree.generate(worldIn, itemRand, pos.up(1))) {
				//playerIn.setHeldItem(hand, null);
			}

		
		/*
			ItemStack stack = player.getHeldItem(hand);
			NBTTagCompound nbt = stack.getOrCreateSubCompound("pos");
			
			if(!nbt.hasKey("x1")) {
				nbt.setInteger("x1", pos.getX());
				nbt.setInteger("y1", pos.getY());
				nbt.setInteger("z1", pos.getZ());
			} else {
				long time = System.nanoTime();
				
				WorldGenGiantRoot root = new WorldGenGiantRoot(new BlockPos(nbt.getInteger("x1"), nbt.getInteger("y1"), nbt.getInteger("z1")), pos, 14);
				root.generate(worldIn, itemRand, pos);
				
				System.out.println("ms: " + (System.nanoTime() - time) / 1000000.0F);
				
				nbt.removeTag("x1");
			}
		*/
			/*
			WorldGenMangroveTree tree = new WorldGenMangroveTree();
			tree.generate(worldIn, itemRand, pos.add(0, 10, 0));
			*/
			/*WorldGenGiantTree tree = new WorldGenGiantTree();
			tree.generate(worldIn, itemRand, pos.add(0, 10, 0));*/
		/*
			WorldGenSmallPortal portal = new WorldGenSmallPortal(player.getHorizontalFacing());
			if(portal.generate(worldIn, itemRand, pos.up())) {
				//playerIn.setHeldItem(hand, null);
			}
		*/

			if(worldIn.getBlockState(pos).getBlock() == BlockRegistry.MOULD_HORN) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
				LocationSporeHive hive = new LocationSporeHive(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos), pos);
				hive.addBounds(new AxisAlignedBB(pos).grow(8 + Math.abs(MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()) % 12), 4, 8 + Math.abs(MathHelper.getCoordinateRandom(pos.getX() + 10, pos.getY(), pos.getZ()) % 12)));
				//hive.addBounds(new AxisAlignedBB(pos).grow(64, 4, 64));
				hive.setSeed(MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ()));
				worldStorage.getLocalStorageHandler().addLocalStorage(hive);
			}
				
			
		} else {
			this.doClientStuff(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		}

		return EnumActionResult.SUCCESS;
	}
	
	@SideOnly(Side.CLIENT)
	private void doClientStuff(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		BLParticles.MOULD_THROBBING.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
