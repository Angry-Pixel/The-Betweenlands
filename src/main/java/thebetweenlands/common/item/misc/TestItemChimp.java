package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.mobs.EntityBigPuffshroom;
import thebetweenlands.common.entity.projectiles.EntitySkySpores;


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
		
				WorldGenUnderwaterRuins ruins = new WorldGenUnderwaterRuins();
				ruins.generate(worldIn, itemRand, pos);
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
		
		/*	WorldGenWightFortress fortress = new WorldGenWightFortress();
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

		*/
		/*	WorldGenCragrockTower tower = new WorldGenCragrockTower();

			if(tower.generate(worldIn, itemRand, pos.up(8).add(8, 0, 0))) {
				//playerIn.setHeldItem(hand, null);
			}
		 	
		*/

		/*
			WorldGenNibbletwigTree tree = new WorldGenNibbletwigTree();
			if(tree.generate(worldIn, itemRand, pos.up(1))) {
				//playerIn.setHeldItem(hand, null);
			}
		
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
		/*		WorldGenSludgeWormDungeon dungeon = new WorldGenSludgeWormDungeon();
			//dungeon.makeMaze(worldIn, itemRand, pos.up().add(1, 0, 1));
			//dungeon.generate(worldIn, itemRand, pos.up(59).add(3, 0, 3)); //generates up
			dungeon.generate(worldIn, itemRand, pos.up(1).add(3, 0, 3)); //generates down
			//dungeon.generateTower(worldIn, itemRand, pos.add(15, 0, 15));
			//dungeon.generateDecayPit(worldIn, itemRand, pos.up(14));
	
		
	
			EntityDecayPitTarget target = new EntityDecayPitTarget(worldIn);
			target.setPosition(pos.getX() + 0.5F, pos.getY() + 8, pos.getZ() + 0.5F);
			worldIn.spawnEntity(target);
	
			
	


*/
	/*		EntitySplodeshroom trap = new EntitySplodeshroom(worldIn);
			BlockPos offset = pos.offset(facing);
			trap.setPosition(offset.getX() + 0.5F, offset.getY(), offset.getZ() + 0.5F);
			worldIn.spawnEntity(trap);
		*/
			/*	
			EntityCCGroundSpawner trap = new EntityCCGroundSpawner(worldIn);
			trap.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			trap.onInitialSpawn(worldIn.getDifficultyForLocation(trap.getPosition()), null);
			worldIn.spawnEntity(trap);*/
			/*
				EntityMovingWall wall = new EntityMovingWall(worldIn);
				wall.setPosition(pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F);
				//wall.motionZ = 0.05F; //X or Z movement - renderer rotates automagic atm
				worldIn.spawnEntity(wall);
			
				
				WorldGenChiromawNest nest = new WorldGenChiromawNest();
				nest .generate(worldIn, itemRand, pos.up());
			
//				EntityRockSnot snot = new EntityRockSnot(worldIn);
//				snot.setPosition(pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F);
//				worldIn.spawnEntity(snot);

			EntityAnimalBurrow burrow = new EntityAnimalBurrow(worldIn);
			burrow.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			if (burrow.getCanSpawnHere()) {
				burrow.onInitialSpawn(worldIn.getDifficultyForLocation(burrow.getPosition()), null);
				worldIn.spawnEntity(burrow);
			}
			*/

				//makeSomethingHere(worldIn, player);
				
			EntityBigPuffshroom bigPuffShroom = new EntityBigPuffshroom(worldIn);
			bigPuffShroom.setPosition(pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F);
			worldIn.spawnEntity(bigPuffShroom);
			
		//	EntitySporeMinion spore = new EntitySporeMinion(worldIn);
		//	spore.setPosition(pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F);
		//	spore.setType(0);
		//	worldIn.spawnEntity(spore);
				
			//EntitySpyEye spy_eye = new EntitySpyEye(worldIn);
			//spy_eye.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			//worldIn.spawnEntity(spy_eye);
		}

		return EnumActionResult.SUCCESS;
	}

	//Test code for things
	private void makeSomethingHere(World world, EntityPlayer player) {
			if (!world.isRemote) {// && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
					if (world.canBlockSeeSky(player.getPosition())) {
						double angle = Math.toRadians((double)world.rand.nextInt(360));
						double offSetX = -Math.sin(angle) * 88D;
						double offSetZ = Math.cos(angle) * 88D;
						EntitySkySpores skySpores = new EntitySkySpores(world, player.posX + offSetX, 96, player.posZ + offSetZ, player);
						world.spawnEntity(skySpores);
					}
					System.out.println("Summoning Panspermia");
			}
	}

	protected AxisAlignedBB proximityBox(EntityPlayer player) {
		return new AxisAlignedBB(player.getPosition()).grow(8D, 2D, 8D);
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
