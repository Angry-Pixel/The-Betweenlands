package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSludgeWormDungeon;


//MINE!!
public class TestItemChimp extends Item {
	public TestItemChimp() {
		this.setMaxStackSize(1);
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
		
		/*	WorldGenCragrockTower tower = new WorldGenCragrockTower();

		/*
			WorldGenCragrockTower tower = new WorldGenCragrockTower();

			if(tower.generate(worldIn, itemRand, pos.up(8).add(8, 0, 0))) {
				//playerIn.setHeldItem(hand, null);
			}


		
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
	


			WorldGenSludgeWormDungeon dungeon = new WorldGenSludgeWormDungeon();
			//dungeon.makeMaze(worldIn, itemRand, pos.up().add(1, 0, 1));
			//dungeon.generate(worldIn, itemRand, pos.up(59).add(3, 0, 3)); //generates up
			dungeon.generate(worldIn, itemRand, pos.up(1).add(3, 0, 3)); //generates down
			//dungeon.generateTower(worldIn, itemRand, pos.add(15, 0, 15));
			//dungeon.generateDecayPit(worldIn, itemRand, pos.up(14));
/*



		EntityDecayPitTarget target = new EntityDecayPitTarget(worldIn);
			
				EntityDecayPitChain chain5 = new EntityDecayPitChain(worldIn);
				EntityDecayPitChain chain6 = new EntityDecayPitChain(worldIn);
				EntityDecayPitChain chain7 = new EntityDecayPitChain(worldIn);
				EntityDecayPitChain chain8 = new EntityDecayPitChain(worldIn);

				chain5.setLength(5);
				chain6.setLength(5);
				chain7.setLength(5);
				chain8.setLength(5);

				// S = 0, W = 1, N = 2, E = 3

				chain5.setFacing(1);
				chain6.setFacing(0);
				chain7.setFacing(3);
				chain8.setFacing(2);
				
				chain5.setPosition(pos.getX() + 0.5F, pos.getY() +10F, pos.getZ() - 11.5F);
				chain6.setPosition(pos.getX() + 12.5F, pos.getY() +10F, pos.getZ() + 0.5F);
				chain7.setPosition(pos.getX() + 0.5F, pos.getY() +10F, pos.getZ() + 12.5F);
				chain8.setPosition(pos.getX() - 11.5F, pos.getY() +10F, pos.getZ() + 0.5F);

				target.setPosition(pos.getX() + 0.5F, pos.getY() + 8, pos.getZ() + 0.5F);
				
				worldIn.spawnEntity(target);

				worldIn.spawnEntity(chain5);
				worldIn.spawnEntity(chain6);
				worldIn.spawnEntity(chain7);
				worldIn.spawnEntity(chain8);

		*/
/*
				EntityTinyWormEggSac sac = new EntityTinyWormEggSac(worldIn);
				sac.setPosition(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F);
				worldIn.spawnEntity(sac);

				
				EntitySplodeshroom trap = new EntitySplodeshroom(worldIn);
				BlockPos offset = pos.offset(facing);
				trap.setPosition(offset.getX() + 0.5F, offset.getY(), offset.getZ() + 0.5F);
				worldIn.spawnEntity(trap);*/
				
		}

		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
