package thebetweenlands.blocks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.container.BlockAlembic;
import thebetweenlands.blocks.container.BlockAnimator;
import thebetweenlands.blocks.container.BlockBLDualFurnace;
import thebetweenlands.blocks.container.BlockBLFurnace;
import thebetweenlands.blocks.container.BlockBLWorkbench;
import thebetweenlands.blocks.container.BlockCompostBin;
import thebetweenlands.blocks.container.BlockGeckoCage;
import thebetweenlands.blocks.container.BlockInfuser;
import thebetweenlands.blocks.container.BlockItemShelf;
import thebetweenlands.blocks.container.BlockLootPot1;
import thebetweenlands.blocks.container.BlockLootPot2;
import thebetweenlands.blocks.container.BlockLootPot3;
import thebetweenlands.blocks.container.BlockPestleAndMortar;
import thebetweenlands.blocks.container.BlockPurifier;
import thebetweenlands.blocks.container.BlockTarLootPot1;
import thebetweenlands.blocks.container.BlockTarLootPot2;
import thebetweenlands.blocks.container.BlockTarLootPot3;
import thebetweenlands.blocks.container.BlockWeedWoodChest;
import thebetweenlands.blocks.ores.BlockGenericOre;
import thebetweenlands.blocks.ores.BlockMiddleGemOre;
import thebetweenlands.blocks.plants.BlockAlgae;
import thebetweenlands.blocks.plants.BlockBLHanger;
import thebetweenlands.blocks.plants.BlockBLSmallPlants;
import thebetweenlands.blocks.plants.BlockBlackHatMushroom;
import thebetweenlands.blocks.plants.BlockBlubCappedMushroomHead;
import thebetweenlands.blocks.plants.BlockBlubCappedMushroomStalk;
import thebetweenlands.blocks.plants.BlockBogBean;
import thebetweenlands.blocks.plants.BlockBulbCappedMushroom;
import thebetweenlands.blocks.plants.BlockCaveGrass;
import thebetweenlands.blocks.plants.BlockCaveMoss;
import thebetweenlands.blocks.plants.BlockDeepWaterCoral;
import thebetweenlands.blocks.plants.BlockDoubleHeightPlant;
import thebetweenlands.blocks.plants.BlockFlatHeadMushroom;
import thebetweenlands.blocks.plants.BlockGoldenClub;
import thebetweenlands.blocks.plants.BlockMarshMarigold;
import thebetweenlands.blocks.plants.BlockMireCoral;
import thebetweenlands.blocks.plants.BlockPitcherPlant;
import thebetweenlands.blocks.plants.BlockPoisonIvy;
import thebetweenlands.blocks.plants.BlockSwampKelp;
import thebetweenlands.blocks.plants.BlockSwampPlant;
import thebetweenlands.blocks.plants.BlockSwampReed;
import thebetweenlands.blocks.plants.BlockSwampReedUW;
import thebetweenlands.blocks.plants.BlockThorns;
import thebetweenlands.blocks.plants.BlockVenusFlyTrap;
import thebetweenlands.blocks.plants.BlockVolarpad;
import thebetweenlands.blocks.plants.BlockWallPlants;
import thebetweenlands.blocks.plants.BlockWaterFlower;
import thebetweenlands.blocks.plants.BlockWaterFlowerStalk;
import thebetweenlands.blocks.plants.BlockWaterWeeds;
import thebetweenlands.blocks.plants.BlockWeedWoodBush;
import thebetweenlands.blocks.plants.BlockWeepingBlue;
import thebetweenlands.blocks.plants.crops.BlockAspectCrop;
import thebetweenlands.blocks.plants.crops.BlockBLGenericCrop;
import thebetweenlands.blocks.plants.roots.BlockRoot;
import thebetweenlands.blocks.plants.roots.BlockRootUW;
import thebetweenlands.blocks.stalactite.BlockStalactite;
import thebetweenlands.blocks.structure.BlockDruidAltar;
import thebetweenlands.blocks.structure.BlockDruidSpawner;
import thebetweenlands.blocks.structure.BlockDruidStone;
import thebetweenlands.blocks.structure.BlockTarBeastSpawner;
import thebetweenlands.blocks.terrain.BlockBLFallenLeaves;
import thebetweenlands.blocks.terrain.BlockBetweenlandsBedrock;
import thebetweenlands.blocks.terrain.BlockBetweenstone;
import thebetweenlands.blocks.terrain.BlockDeadGrass;
import thebetweenlands.blocks.terrain.BlockDentrothyst;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.blocks.terrain.BlockGenericStone;
import thebetweenlands.blocks.terrain.BlockMud;
import thebetweenlands.blocks.terrain.BlockPeat;
import thebetweenlands.blocks.terrain.BlockPitstone;
import thebetweenlands.blocks.terrain.BlockPuddle;
import thebetweenlands.blocks.terrain.BlockSilt;
import thebetweenlands.blocks.terrain.BlockSlimyDirt;
import thebetweenlands.blocks.terrain.BlockSlimyGrass;
import thebetweenlands.blocks.terrain.BlockSludgyDirt;
import thebetweenlands.blocks.terrain.BlockStagnantWater;
import thebetweenlands.blocks.terrain.BlockSwampDirt;
import thebetweenlands.blocks.terrain.BlockSwampGrass;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.blocks.terrain.BlockTarFluid;
import thebetweenlands.blocks.terrain.BlockWisp;
import thebetweenlands.blocks.terrain.lifeCrystal.BlockLifeCrystalOre;
import thebetweenlands.blocks.tree.BlockBLLeaves;
import thebetweenlands.blocks.tree.BlockBLLog;
import thebetweenlands.blocks.tree.BlockBLPortalFrame;
import thebetweenlands.blocks.tree.BlockBLSapling;
import thebetweenlands.blocks.tree.BlockHollowLog;
import thebetweenlands.blocks.tree.BlockRubberLog;
import thebetweenlands.blocks.tree.BlockTreeFungus;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.entities.mobs.EntityTermite;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.block.ItemBlockSlab;
import thebetweenlands.items.herblore.ItemGenericPlantDrop;
import thebetweenlands.items.herblore.ItemGenericPlantDrop.EnumItemPlantDrop;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.world.feature.trees.WorldGenRubberTree;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

public class BLBlockRegistry {
	// LIST WITH ALL BLOCKS IN THIS CLASS
	public static final List<Block> BLOCKS = new LinkedList<Block>();

	// PORTAL
	public static BlockTreePortal treePortalBlock = new BlockTreePortal();

	// TERRAIN
	public static final Block betweenstone = new BlockBetweenstone();
	public static final Block pitstone = new BlockPitstone();
	public static final Block limestone = new BlockBLGenericDeco("limestone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block genericStone = new BlockGenericStone();
	public static final Block druidStone1 = new BlockDruidStone("druidStone1");
	public static final Block druidStone2 = new BlockDruidStone("druidStone2");
	public static final Block druidStone3 = new BlockDruidStone("druidStone3");
	public static final Block druidStone4 = new BlockDruidStone("druidStone4");
	public static final Block druidStone5 = new BlockDruidStone("druidStone5");
	public static final Block betweenlandsBedrock = new BlockBetweenlandsBedrock();
	public static final Block swampDirt = new BlockSwampDirt();
	public static final Block swampGrass = new BlockSwampGrass();
	public static final Block deadGrass = new BlockDeadGrass();
	public static final Block silt = new BlockSilt();
	public static final Block mud = new BlockMud();
	public static final Block swampWater = new BlockSwampWater();
	public static final Block algae = new BlockAlgae();
	public static final Block sulfurTorch = new BlockSulfurTorch();
	public static final Block dampTorch = new BlockDampTorch();
	public static final Block peat = new BlockPeat();
	public static final BlockWisp wisp = new BlockWisp();
	public static final BlockSludge sludge = new BlockSludge();
	public static final Block fallenLeaves = new BlockBLFallenLeaves("fallenLeaves");
	public static final Block purpleFallenLeaves = new BlockBLFallenLeaves("purpleFallenLeaves");
	public static final Block slimyGrass = new BlockSlimyGrass();
	public static final Block slimyDirt = new BlockSlimyDirt();
	public static final Block sludgyDirt = new BlockSludgyDirt();
	public static final Block tarFluid = new BlockTarFluid();
	public static final Block stagnantWaterFluid = new BlockStagnantWater();
	public static final Block puddle = new BlockPuddle();
	public static final BlockFarmedDirt farmedDirt = new BlockFarmedDirt();
	public static final Block dentrothyst = new BlockDentrothyst();

	// ORES @Params (name/texture, drops this item)
	// hardness & resistance could be set on an individual basis but aren't atm.
	public static final Block aquaMiddleGemOre = new BlockMiddleGemOre("aquaMiddleGemOre", null);
	public static final Block crimsonMiddleGemOre = new BlockMiddleGemOre("crimsonMiddleGemOre", null);
	public static final Block greenMiddleGemOre = new BlockMiddleGemOre("greenMiddleGemOre", null);
	public static final Block octineOre = new BlockGenericOre("octineOre", null){
		@Override
		public void spawnParticle(World world, double x, double y, double z) { 
			BLParticle.FLAME.spawn(world, x, y, z, 0, 0, 0, 0);
		}
	}.setLightLevel(0.875F); //setting null drops item block
	public static final Block syrmoriteOre = new BlockGenericOre("syrmoriteOre", null);
	public static final Block boneOre = new BlockGenericOre("boneOre", ItemGeneric.createStack(EnumItemGeneric.SLIMY_BONE)).setXP(5, 12);
	public static final Block sulfurOre = new BlockGenericOre("sulfurOre", ItemGeneric.createStack(EnumItemGeneric.SULFUR)){
		@Override
		public void spawnParticle(World world, double x, double y, double z) { 
			BLParticle.SULFUR_ORE.spawn(world, x, y, z, 0, 0, 0, 0);
		}
	}.setXP(2, 5);
	public static final Block valoniteOre = new BlockGenericOre("valoniteOre", ItemGeneric.createStack(EnumItemGeneric.VALONITE_SHARD)).setXP(5, 12);
	public static final BlockLifeCrystalOre lifeCrystalOre = (BlockLifeCrystalOre) new BlockLifeCrystalOre().setLightLevel(0.4F);


	// TREES
	public static final Block saplingWeedwood = new BlockBLSapling("saplingWeedwood").setTreeGenerator(new WorldGenWeedWoodTree());
	public static final Block saplingSapTree = new BlockBLSapling("saplingSapTree").setTreeGenerator(new WorldGenSapTree());
	public static final Block saplingSpiritTree = new BlockBLSapling("saplingSpiritTree");
	public static final Block saplingRubberTree = new BlockBLSapling("saplingRubberTree").setTreeGenerator(new WorldGenRubberTree());
	public static final Block saplingPurpleRain = new BlockBLSapling("saplingPurpleRain");

	public static final Block weedwoodLeaves = new BlockBLLeaves("weedwoodLeaves"){
		@Override
		public Item getItemDropped(int meta, Random rand, int fortune) {
			return Item.getItemFromBlock(BLBlockRegistry.saplingWeedwood);
		}
	}.setHasSpoopyTexture(true);
	public static final BlockBLLeaves sapTreeLeaves = new BlockBLLeaves("sapTreeLeaves"){
		@Override
		public Item getItemDropped(int meta, Random rand, int fortune) {
			return Item.getItemFromBlock(BLBlockRegistry.saplingSapTree);
		}
	}.setHasSpoopyTexture(true);
	public static final Block rubberTreeLeaves = new BlockBLLeaves("rubberTreeLeaves"){
		@Override
		public Item getItemDropped(int meta, Random rand, int fortune) {
			return Item.getItemFromBlock(BLBlockRegistry.saplingRubberTree);
		}
	}.setHasSpoopyTexture(true);
	//public static final Block spiritTreeLeaves = new BlockBLLeaves("spiritTreeLeaves"); - not sure about these
	public static final Block purpleRainLeavesLight = new BlockBLLeaves("purpleRainLeavesLight"){
		@Override
		public Item getItemDropped(int meta, Random rand, int fortune) {
			return Item.getItemFromBlock(BLBlockRegistry.saplingPurpleRain);
		}
	};
	public static final Block purpleRainLeavesDark = new BlockBLLeaves("purpleRainLeavesDark"){
		@Override
		public Item getItemDropped(int meta, Random rand, int fortune) {
			return Item.getItemFromBlock(BLBlockRegistry.saplingPurpleRain);
		}
	};

	public static final Block weedwoodLog = new BlockBLLog("weedwoodLog").setHasSpoopyTexture(true);
	public static final Block weedwood = new BlockBLLog("weedwood").setHasSperateTopIcon(false);
	public static final Block weedwoodBark = new BlockBLLog("weedwoodBark").setHasSperateTopIcon(false);
	public static final Block rottenWeedwoodBark = new BlockBLLog("rottenWeedwoodBark") {
		@Override
		public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
			if (!world.isRemote) {
				if (world.rand.nextInt(4) == 0) {
					EntityTermite entity = new EntityTermite(world);
					entity.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
					world.spawnEntityInWorld(entity);
				}
			}
			super.onBlockDestroyedByPlayer(world, x, y, z, meta);
		}	 
	}.setHasSperateTopIcon(false);
	public static final BlockBLLog sapTreeLog = new BlockBLLog("sapTreeLog"){
		@Override
		public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
			drops.add(new ItemStack(BLItemRegistry.sapBall, 1 + world.rand.nextInt(2 + fortune)));
			return drops;
		}

		@Override
		public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
			return true;
		}
	}.setHasSpoopyTexture(true);
	public static final Block rubberTreeLog = new BlockRubberLog("rubberTreeLog").setHasSpoopyTexture(true);
	public static final Block weedwoodBush = new BlockWeedWoodBush().setBlockName("thebetweenlands.weedwoodBush").setCreativeTab(ModCreativeTabs.plants);
	public static final Block portalBark = new BlockBLLog("portalBark").setHasSperateTopIcon(false);
	public static final Block portalBarkFrame = new BlockBLPortalFrame();
	public static final Block purpleRainLog = new BlockBLLog("purpleRainLog");

	// WOOD
	public static final Block weedwoodPlanks = new BlockGenericPlanks("weedwoodPlanks", Material.wood);
	public static final Block rubberTreePlanks = new BlockGenericPlanks("rubberTreePlanks", Material.wood);
	public static final Block purpleRainPlanks = new BlockGenericPlanks("purpleRainPlanks", Material.wood);
	public static final Block hollowLog = new BlockHollowLog();

	// DOUBLE PLANTS
	public static final BlockDoubleHeightPlant sundew = new BlockDoubleHeightPlant("Sundew", 0.8F) {
		@Override
		@SideOnly(Side.CLIENT)
		public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
			if (world.rand.nextInt(35) == 0) {
				BLParticle.FLY.spawn(world, x, y + 1, z);
			}
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void registerBlockIcons(IIconRegister reg) {
			this.topIcon = reg.registerIcon("thebetweenlands:doublePlant" + name + "Top");
		}
	}.setRenderType(BlockRenderIDs.MODEL_PLANT.id()).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SUNDEW_HEAD));

	public static final Block doubleSwampTallgrass = new BlockDoubleHeightPlant("DoubleSwampTallgrass", 0.8F).setHasSpoopyTexture(true).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES));
	public static final Block phragmites = new BlockDoubleHeightPlant("Phragmites", 0.8F) {
		@Override
		@SideOnly(Side.CLIENT)
		public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
			if (world.rand.nextInt(15) == 0) {
				if (world.rand.nextInt(6) != 0) {
					BLParticle.FLY.spawn(world, x, y, z);
				} else {
					BLParticle.MOTH.spawn(world, x, y, z);
				}
			}
		}
	}.setHasSpoopyTexture(true).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.PHRAGMITE_STEMS));
	public static final Block tallCattail = new BlockDoubleHeightPlant("TallCattail", 0.8F).setHasSpoopyTexture(true).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CATTAIL_HEAD));
	public static final Block cardinalFlower = new BlockDoubleHeightPlant("CardinalFlower", 0.8F).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CARDINAL_FLOWER_PETALS));
	public static final Block broomsedge = new BlockDoubleHeightPlant("BroomSedge", 0.8F).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BROOM_SEDGE_LEAVES));
	public static final BlockWeepingBlue weepingBlue = new BlockWeepingBlue();
	public static final BlockPitcherPlant pitcherPlant = (BlockPitcherPlant) new BlockPitcherPlant().setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.PITCHER_PLANT_TRAP));

	//PLANTS
	public static final BlockSwampReed swampReed = new BlockSwampReed();
	public static final BlockSwampReedUW swampReedUW = new BlockSwampReedUW();
	public static final BlockBogBean bogBean = new BlockBogBean();
	public static final BlockGoldenClub goldenClub = new BlockGoldenClub();
	public static final BlockMarshMarigold marshMarigold = new BlockMarshMarigold();
	public static final BlockSwampKelp swampKelp = new BlockSwampKelp();
	public static final BlockWaterWeeds waterWeeds = new BlockWaterWeeds();
	public static final BlockMireCoral mireCoral = new BlockMireCoral();
	public static final BlockDeepWaterCoral deepWaterCoral = new BlockDeepWaterCoral();
	public static final BlockWaterFlowerStalk waterFlowerStalk = new BlockWaterFlowerStalk();
	public static final BlockWaterFlower waterFlower = new BlockWaterFlower();
	public static final BlockRootUW rootUW = new BlockRootUW();
	public static final BlockRoot root = new BlockRoot();
	public static final BlockBlackHatMushroom blackHatMushroom = new BlockBlackHatMushroom();
	public static final BlockFlatHeadMushroom flatHeadMushroom = new BlockFlatHeadMushroom();
	public static final BlockBulbCappedMushroom bulbCappedMushroom = new BlockBulbCappedMushroom();
	public static final BlockSwampPlant swampPlant = (BlockSwampPlant) new BlockSwampPlant().setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.GENERIC_LEAF));
	public static final BlockVenusFlyTrap venusFlyTrap = (BlockVenusFlyTrap) new BlockVenusFlyTrap().setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.VENUS_FLY_TRAP));
	public static final BlockVolarpad volarpad = (BlockVolarpad) new BlockVolarpad().setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.VOLARPAD));
	public static final BlockThorns thorns = new BlockThorns();
	public static final BlockPoisonIvy poisonIvy = new BlockPoisonIvy();
	public static final Block wallPlants = new BlockWallPlants();

	public static final Block bulbCappedMushroomStalk = new BlockBlubCappedMushroomStalk();
	public static final Block bulbCappedMushroomTop = new BlockBlubCappedMushroomHead();

	// CROPS
	public static final BlockBLGenericCrop middleFruitBush = new BlockBLGenericCrop("middleFruitBush"){
		@Override
		public ItemStack getSeedDrops(World world, int x, int y, int z) {
			return new ItemStack(BLItemRegistry.middleFruitSeeds, 1, 0);
		}

		@Override
		public ItemStack getCropDrops(World world, int x, int y, int z) {
			return new ItemStack(BLItemRegistry.middleFruit, 1, 0);
		}
	};
	public static final BlockBLGenericCrop fungusCrop = new BlockBLGenericCrop("fungusCrop") {
		@Override
		public ItemStack getSeedDrops(World world, int x, int y, int z) {
			return new ItemStack(BLItemRegistry.spores);
		}

		@Override
		public ItemStack getCropDrops(World world, int x, int y, int z) {
			return new ItemStack(BLItemRegistry.yellowDottedFungus);
		}

		@Override
		public void updateTick(World world, int x, int y, int z, Random rand) {
			if(!world.isRemote && this.isCropOrSoilDecayed(world, x, y, z) && this.isFullyGrown(world, x, y, z) && rand.nextInt(6) == 0) {
				EntityLiving entity = new EntitySporeling(world);
				entity.setLocationAndAngles(x + 0.5D, y + 0.5D, z + 0.5D, 0.0f, 0.0f);
				world.spawnEntityInWorld(entity);
				world.setBlock(x, y, z, Blocks.air);
			} else {
				super.updateTick(world, x, y, z, rand);
			}
		}
	};
	public static final BlockAspectCrop aspectCrop = new BlockAspectCrop("aspectCrop");

	public static final Block caveMoss = new BlockCaveMoss();
	public static final BlockCaveGrass caveGrass = new BlockCaveGrass("caveGrass");

	// SMALL PLANTS
	public static final Block catTail = new BlockBLSmallPlants("cattail").setHasSpoopyTexture(true).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CATTAIL_HEAD));
	public static final Block swampTallGrass = new BlockBLSmallPlants("swampTallgrass").setHasSpoopyTexture(true).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES));
	public static final Block shoots = new BlockBLSmallPlants("shoots").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SHOOT_LEAVES));
	public static final Block nettleFlowered = new BlockBLSmallPlants("nettleFlowered"){
		@Override
		public void updateTick(World world, int x, int y, int z, Random rand) {
			super.updateTick(world, x, y, z, rand);
			if (rand.nextInt(25) == 0) {
				int xx;
				int yy;
				int zz;
				xx = x + rand.nextInt(3) - 1;
				yy = y + rand.nextInt(2) - rand.nextInt(2);
				zz = z + rand.nextInt(3) - 1;
				if (world.isAirBlock(xx, yy, zz) && canBlockStay(world, xx, yy, zz)) {
					world.setBlock(xx, yy, zz, BLBlockRegistry.nettle);
				}
				if (rand.nextInt(40) == 0) {
					world.setBlock(x, y, z, BLBlockRegistry.nettle);
				}
			}
		}

		@Override
		public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
			if(entity instanceof IEntityBL == false) entity.attackEntityFrom(DamageSource.cactus, 1);
		}
	}.setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.NETTLE_LEAF));
	public static final Block nettle = new BlockBLSmallPlants("nettle"){
		@Override
		public void updateTick(World world, int x, int y, int z, Random rand) {
			super.updateTick(world, x, y, z, rand);
			if (rand.nextInt(80) == 0) {
				world.setBlock(x, y, z, BLBlockRegistry.nettle);
			}
		}

		@Override
		public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
			if(entity instanceof IEntityBL == false) entity.attackEntityFrom(DamageSource.cactus, 1);
		}
	}.setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.NETTLE_LEAF));
	public static final Block arrowArum = new BlockBLSmallPlants("arrowArum").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.ARROW_ARUM_LEAF));
	public static final Block buttonBush = new BlockBLSmallPlants("buttonBush").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BUTTON_BUSH_FLOWERS));
	public static final Block marshHibiscus = new BlockBLSmallPlants("marshHibiscus").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MARSH_HIBISCUS_FLOWER));
	public static final Block pickerelWeed = new BlockBLSmallPlants("pickerelWeed").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.PICKEREL_WEED_FLOWER));
	public static final Block softRush = new BlockBLSmallPlants("softRush").setHasSpoopyTexture(true).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SOFT_RUSH_LEAVES));
	public static final Block marshMallow = new BlockBLSmallPlants("marshMallow").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MARSH_MALLOW_FLOWER));
	public static final Block milkweed = new BlockBLSmallPlants("milkweed").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MILK_WEED));
	public static final Block blueIris = new BlockBLSmallPlants("blueIris").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BLUE_IRIS_PETAL));
	public static final Block copperIris = new BlockBLSmallPlants("copperIris").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.COPPER_IRIS_PETALS));
	public static final Block blueEyedGrass = new BlockBLSmallPlants("blueEyedGrass").setHasSpoopyTexture(true).setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BLUE_EYED_GRASS_FLOWERS));
	public static final Block boneset = new BlockBLSmallPlants("boneset").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BONESET_FLOWERS));
	public static final Block bottleBrushGrass = new BlockBLSmallPlants("bottleBrushGrass").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BOTTLE_BRUSH_GRASS_BLADES));
	public static final Block sludgecreep = new BlockBLSmallPlants("sludgecreep").setHarvestedItem(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SLUDGECREEP_LEAVES));
	public static final Block deadWeedwoodBush = new BlockBLSmallPlants("deadWeedwoodBush").setHarvestedItem(ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));

	// UNDERGROWTH
	public static final Block hanger = new BlockBLHanger("hanger");
	public static final Block purpleHanger = new BlockBLHanger("purpleHanger");
	public static final Block purpleHangerFlowered = new BlockBLHanger("purpleHangerFlowered");
	public static final Block treeFungus = new BlockTreeFungus();

	// DECORATIONS AND UTILITIES
	public static final Block weedwoodCraftingTable = new BlockBLWorkbench();
	public static final Block weedwoodChest = new BlockWeedWoodChest();
	public static final Block furnaceBL = new BlockBLFurnace(false).setBlockName("thebetweenlands.furnaceBL");
	public static final Block furnaceBLLit = new BlockBLFurnace(true).setBlockName("thebetweenlands.furnaceBLLit").setLightLevel(0.875F);
	public static final Block dualFurnaceBL = new BlockBLDualFurnace(false).setBlockName("thebetweenlands.dualFurnaceBL");
	public static final Block dualFurnaceBLLit = new BlockBLDualFurnace(true).setBlockName("thebetweenlands.dualFurnaceBLLit").setLightLevel(0.875F);
	public static final Block stalactite = new BlockStalactite(Material.rock).setHardness(0.75F).setStepSound(Block.soundTypeStone).setBlockName("thebetweenlands.stalactite").setCreativeTab(ModCreativeTabs.blocks);
	public static final Block animator = new BlockAnimator();
	public static final Block purifier = new BlockPurifier();
	public static final Block compostBin = new BlockCompostBin();
	public static final Block alembic = new BlockAlembic();
	public static final Block infuser = new BlockInfuser();
	public static final Block pestleAndMortar = new BlockPestleAndMortar();
	public static final Block mudBrick = new BlockBLGenericDeco("mudBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block angryBetweenstone = new BlockBLGenericDeco("angryBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block betweenstoneBricks = new BlockBLGenericDeco("betweenstoneBricks", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block betweenstoneTiles = new BlockBLGenericDeco("betweenstoneTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block chiseledBetweenstone = new BlockBLGenericDeco("chiseledBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block carvedCrag = new BlockBLGenericDeco("carvedCrag", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block cragrockBrick = new BlockBLGenericDeco("cragrockBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block polishedLimestone = new BlockBLGenericDeco("polishedLimestone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block limestoneBricks = new BlockBLGenericDeco("limestoneBricks", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block limestoneTiles = new BlockBLGenericDeco("limestoneTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block chiseledLimestone = new BlockBLGenericDeco("chiseledLimestone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block cragTiles = new BlockBLGenericDeco("cragTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block bronzeCircleBrick = new BlockBLGenericDeco("bronzeCircleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block silverCircleBrick = new BlockBLGenericDeco("silverCircleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block goldCircleBrick = new BlockBLGenericDeco("goldCircleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothBetweenstone = new BlockBLGenericDeco("smoothBetweenstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothCragrock = new BlockBLGenericDeco("smoothCragrock", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block solidTar = new BlockBLGenericDeco("solidTar", Material.rock).setHardness(15F).setResistance(20.0F).setStepSound(Block.soundTypeStone);
	public static final Block sulphurBlock = new BlockBLGenericDeco("sulphurBlock", Material.rock).setHardness(2F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block syrmoriteBlock = new BlockBLGenericDeco("syrmoriteBlock", Material.rock).setHardness(10F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block octineBlock = new BlockBLGenericDeco("octineBlock", Material.rock).setHardness(5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block valoniteBlock = new BlockBLGenericDeco("valoniteBlock", Material.rock).setHardness(10F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block templeBrick = new BlockBLGenericDeco("templeBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block mossyTempleBrick = new BlockBLGenericDeco("mossyTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block crackedTempleBrick = new BlockBLGenericDeco("crackedTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block bloodyTempleBrick = new BlockBLGenericDeco("bloodyTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block smoothTempleBrick = new BlockBLGenericDeco("smoothTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block carvedTempleBrick = new BlockBLGenericDeco("carvedTempleBrick", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block dungeonTile = new BlockBLGenericDeco("dungeonTile", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block dungeonWall = new BlockBLGenericDeco("dungeonWall", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final BlockTemplePillar templePillar = new BlockTemplePillar();
	public static final Block thatch = new BlockBLGenericDeco("thatch", Material.cloth).setHardness(0.5F).setResistance(1.0F).setStepSound(Block.soundTypeGrass);
	public static final Block blockOfCompost = new BlockBLGenericDeco("blockOfCompost", Material.ground).setHardness(2F).setResistance(5.0F).setStepSound(Block.soundTypeGrass);
	public static final Block mireCoralBlock = new BlockBLGenericDeco("mireCoralBlock", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final Block deepWaterCoralBlock = new BlockBLGenericDeco("deepWaterCoralBlock", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone).setLightLevel(0.875F);
	public static final BlockRubberTap rubberTap = new BlockRubberTap();
	public static final Block itemShelf = new BlockItemShelf();
	public static final Block smoothPitstone = new BlockBLGenericDeco("smoothPitstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block pitstoneBricks = new BlockBLGenericDeco("pitstoneBricks", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block pitstoneTiles = new BlockBLGenericDeco("pitstoneTiles", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone);
	public static final Block chiseledPitstone = new BlockBLGenericDeco("chiseledPitstone", Material.rock).setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundTypeStone); 
	public static final Block choca = new BlockBLGenericDeco("choca", Material.rock).setHardness(15F).setResistance(20.0F).setStepSound(Block.soundTypeStone).setBlockTextureName("thebetweenlands:dave");
	public static final Block geckoCage = new BlockGeckoCage();
	public static final Block blockWoodChipPath = new BlockWoodChipPath();
	public static final BlockWalkway blockWalkWay = new BlockWalkway();
	public static final Block blockTotem = new BlockTotem();
	public static final Block siltGlas = new BlockBLGlass();
	public static final Block siltGlasPane = new BlockBLPane("siltGlass", Material.glass, false).setHardness(0.3F).setStepSound(Block.soundTypeGlass);

	// ALTARS
	public static final Block druidAltar = new BlockDruidAltar();

	// STAIRS, SLABS, WALLS, FENCES, SIGNS, ETC.
	public static final Block betweenstoneBrickStairs = new BlockBLStairs(betweenstoneBricks, 0).setBlockName("thebetweenlands.betweenstoneBrickStairs");
	public static final Block mudBrickStairs = new BlockBLStairs(mudBrick, 0).setBlockName("thebetweenlands.mudBrickStairs");
	public static final Block cragrockBrickStairs = new BlockBLStairs(cragrockBrick, 0).setBlockName("thebetweenlands.cragrockBrickStairs");
	public static final Block limestoneBrickStairs = new BlockBLStairs(limestoneBricks, 0).setBlockName("thebetweenlands.limestoneBrickStairs");
	public static final Block limestoneStairs = new BlockBLStairs(limestone, 0).setBlockName("thebetweenlands.limestoneStairs");
	public static final Block smoothBetweenstoneStairs = new BlockBLStairs(smoothBetweenstone, 0).setBlockName("thebetweenlands.smoothBetweenstoneStairs"); 
	public static final Block smoothCragrockStairs = new BlockBLStairs(smoothCragrock, 0).setBlockName("thebetweenlands.smoothCragrockStairs");
	public static final Block smoothPitstoneStairs = new BlockBLStairs(smoothPitstone, 0).setBlockName("thebetweenlands.smoothPitstoneStairs");
	public static final Block solidTarStairs = new BlockBLStairs(solidTar, 0).setBlockName("thebetweenlands.solidTarStairs");
	public static final Block templeBrickStairs = new BlockBLStairs(templeBrick, 0).setBlockName("thebetweenlands.templeBrickStairs");
	public static final Block weedwoodPlankStairs = new BlockBLStairs(weedwoodPlanks, 0).setBlockName("thebetweenlands.weedwoodPlankStairs");
	public static final Block rubberTreePlankStairs = new BlockBLStairs(rubberTreePlanks, 0).setBlockName("thebetweenlands.rubberTreePlankStairs");
	public static final Block purpleRainPlankStairs = new BlockBLStairs(purpleRainPlanks, 0).setBlockName("thebetweenlands.purpleRainPlankStairs");
	public static final Block pitstoneBrickStairs = new BlockBLStairs(pitstoneBricks, 0).setBlockName("thebetweenlands.pitstoneBrickStairs");
	public static final Block betweenstoneBrickWall = new BlockBLWall(betweenstoneBricks, 0).setBlockName("thebetweenlands.betweenstoneBrickWall");
	public static final Block mudBrickWall = new BlockBLWall(mudBrick, 0).setBlockName("thebetweenlands.mudBrickWall");
	public static final Block cragrockWall = new BlockBLWall(cragrockBrick, 0).setBlockName("thebetweenlands.cragrockWall");
	public static final Block limestoneBrickWall = new BlockBLWall(limestoneBricks, 0).setBlockName("thebetweenlands.limestoneBrickWall");
	public static final Block limestoneWall = new BlockBLWall(limestone, 0).setBlockName("thebetweenlands.limestoneWall");
	public static final Block smoothBetweenstoneWall = new BlockBLWall(smoothBetweenstone, 0).setBlockName("thebetweenlands.smoothBetweenstoneWall");
	public static final Block smoothCragrockWall = new BlockBLWall(smoothCragrock, 0).setBlockName("thebetweenlands.smoothCragrockWall");
	public static final Block smoothPitstoneWall = new BlockBLWall(smoothPitstone, 0).setBlockName("thebetweenlands.smoothPitstoneWall");
	public static final Block solidTarWall = new BlockBLWall(solidTar, 0).setBlockName("thebetweenlands.solidTarWall");
	public static final Block templeBrickWall = new BlockBLWall(templeBrick, 0).setBlockName("thebetweenlands.templeBrickWall");
	public static final Block pitstoneBrickWall = new BlockBLWall(pitstoneBricks, 0).setBlockName("thebetweenlands.pitstoneBrickWall");  
	public static final Block weedwoodPlankFence = new BlockBLFence("weedwoodPlanks", Material.wood).setBlockName("thebetweenlands.weedwoodPlankFence");
	public static final BlockBLFence rubberTreePlankFence = (BlockBLFence) new BlockBLFence("rubberTreePlanks", Material.wood).setBlockName("thebetweenlands.rubberTreePlankFence");
	public static final Block purpleRainPlankFence = new BlockBLFence("purpleRainPlanks", Material.wood).setBlockName("thebetweenlands.purpleRainPlankFence");
	public static final Block weedwoodPlankFenceGate = new BlockBLFenceGate("weedwoodPlanks");
	public static final Block rubberTreePlankFenceGate = new BlockBLFenceGate("rubberTreePlanks");
	public static final Block purpleRainPlankFenceGate = new BlockBLFenceGate("purpleRainPlanks");
	public static final Block weedwoodJukebox = new BlockBLJukebox("weedwood");
	public static final Block weedwoodLadder = new BlockBLLadder("weedwood");
	public static final BlockBLSign weedwoodSignStanding = new BlockBLSign(true, "weedwood");
	public static final BlockBLSign weedwoodWallSign = new BlockBLSign(false, "weedwood");
	public static final Block weedwoodPlankButton = new BlockBLButton("weedwoodPlanks", true);
	public static final Block betweenstoneButton = new BlockBLButton("smoothBetweenstone", false);
	public static final Block weedwoodPlankPressurePlate = new BlockBLPressurePlate("weedwoodPlanks", Material.wood, BlockPressurePlate.Sensitivity.everything);
	public static final Block betweenstonePressurePlate = new BlockBLPressurePlate("betweenstone", Material.rock, BlockPressurePlate.Sensitivity.mobs);
	public static final Block syrmoritePressurePlate = new BlockBLPressurePlate("syrmoriteBlock", Material.rock, BlockPressurePlate.Sensitivity.players);
	public static final Block weedwoodLever = new BlockBLLever();
	public static final BlockBLFlowerPot mudFlowerPot = new BlockBLFlowerPot();
	public static final Block lootPot1 = new BlockLootPot1();
	public static final Block lootPot2 = new BlockLootPot2();
	public static final Block lootPot3 = new BlockLootPot3();
	public static final Block tarLootPot1 = new BlockTarLootPot1();
	public static final Block tarLootPot2 = new BlockTarLootPot2();
	public static final Block tarLootPot3 = new BlockTarLootPot3();
	public static final BlockMossBed mossBed = new BlockMossBed();

	//DOORS
	public static final Block doorWeedwood = new BlockBLDoor("weedwood", Material.wood);
	public static final Block doorSyrmorite = new BlockBLDoor("syrmorite", Material.rock);
	public static final Block trapDoorWeedwood = new BlockBLTrapDoor("weedwood", Material.wood);
	public static final Block trapDoorSyrmorite = new BlockBLTrapDoor("syrmorite", Material.rock);

	// OTHER THINGS
	public static final Block druidSpawner = new BlockDruidSpawner("darkDruid");
	public static final Block tarBeastSpawner = new BlockTarBeastSpawner();
	public static final BlockLifeCrystalBlock lifeCrystalBlock = new BlockLifeCrystalBlock();
	public static final BlockBLSpawner blSpawner = new BlockBLSpawner();
	public static final BlockRope rope = new BlockRope();

	// SLABS
	public static final BlockBLSlab weedwoodPlankSlab = new BlockBLSlab(false, Material.wood, "weedwoodPlanks", null, "axe");
	public static final BlockBLSlab weedwoodPlankSlabDouble = new BlockBLSlab(true, Material.wood, "weedwoodPlanks", weedwoodPlankSlab, "axe");
	public static final BlockBLSlab rubberTreePlankSlab = new BlockBLSlab(false, Material.wood, "rubberTreePlanks", null, "axe");
	public static final BlockBLSlab rubberTreePlankSlabDouble = new BlockBLSlab(true, Material.wood, "rubberTreePlanks", rubberTreePlankSlab, "axe");
	public static final BlockBLSlab purpleRainPlankSlab = new BlockBLSlab(false, Material.wood, "purpleRainPlanks", null, "axe");
	public static final BlockBLSlab purpleRainPlankSlabDouble = new BlockBLSlab(true, Material.wood, "purpleRainPlanks", purpleRainPlankSlab, "axe");
	public static final BlockBLSlab betweenstoneBrickSlab = new BlockBLSlab(false, Material.rock, "betweenstoneBricks", null, "pickaxe");
	public static final BlockBLSlab betweenstoneBrickSlabDouble = new BlockBLSlab(true, Material.rock, "betweenstoneBricks", betweenstoneBrickSlab, "pickaxe");
	public static final BlockBLSlab mudBrickSlab = new BlockBLSlab(false, Material.rock, "mudBrick", null, "pickaxe");
	public static final BlockBLSlab mudBrickSlabDouble = new BlockBLSlab(true, Material.rock, "mudBrick", mudBrickSlab, "pickaxe");
	public static final BlockBLSlab cragrockBrickSlab = new BlockBLSlab(false, Material.rock, "cragrockBrick", null, "pickaxe");
	public static final BlockBLSlab cragrockBrickSlabDouble = new BlockBLSlab(true, Material.rock, "cragrockBrick", cragrockBrickSlab, "pickaxe");
	public static final BlockBLSlab limestoneBrickSlab = new BlockBLSlab(false, Material.rock, "limestoneBricks", null, "pickaxe");
	public static final BlockBLSlab limestoneBrickSlabDouble = new BlockBLSlab(true, Material.rock, "limestoneBricks", limestoneBrickSlab, "pickaxe");
	public static final BlockBLSlab limestoneSlab = new BlockBLSlab(false, Material.rock, "limestone", null, "pickaxe");
	public static final BlockBLSlab limestoneSlabDouble = new BlockBLSlab(true, Material.rock, "limestone", limestoneSlab, "pickaxe");
	public static final BlockBLSlab smoothBetweenstoneSlab = new BlockBLSlab(false, Material.rock, "smoothBetweenstone", null, "pickaxe");
	public static final BlockBLSlab smoothBetweenstoneSlabDouble = new BlockBLSlab(true, Material.rock, "smoothBetweenstone", smoothBetweenstoneSlab, "pickaxe");
	public static final BlockBLSlab smoothCragrockSlab = new BlockBLSlab(false, Material.rock, "smoothCragrock", null, "pickaxe");
	public static final BlockBLSlab smoothCragrockSlabDouble = new BlockBLSlab(true, Material.rock, "smoothCragrock", smoothCragrockSlab, "pickaxe");
	public static final BlockBLSlab smoothPitstoneSlab = new BlockBLSlab(false, Material.rock, "smoothPitstone", null, "pickaxe");
	public static final BlockBLSlab smoothPitstoneSlabDouble = new BlockBLSlab(true, Material.rock, "smoothPitstone", smoothPitstoneSlab, "pickaxe");
	public static final BlockBLSlab solidTarSlab = new BlockBLSlab(false, Material.rock, "solidTar", null, "pickaxe");
	public static final BlockBLSlab solidTarSlabDouble = new BlockBLSlab(true, Material.rock, "solidTar", solidTarSlab, "pickaxe");
	public static final BlockBLSlab templeBrickSlab = new BlockBLSlab(false, Material.rock, "templeBrick", null, "pickaxe");
	public static final BlockBLSlab templeBrickSlabDouble = new BlockBLSlab(true, Material.rock, "templeBrick", templeBrickSlab, "pickaxe");
	public static final BlockBLSlab pitstoneBrickSlab = new BlockBLSlab(false, Material.rock, "pitstoneBricks", null, "pickaxe");
	public static final BlockBLSlab pitstoneBrickSlabDouble = new BlockBLSlab(true, Material.rock, "pitstoneBricks", pitstoneBrickSlab, "pickaxe");
	public static final BlockBLSlab thatchSlab = new BlockBLSlab(false, Material.cloth, "thatch", null, "axe");
	public static final BlockBLSlab thatchSlabDouble = new BlockBLSlab(true, Material.cloth, "thatch", thatchSlab, "axe");

	public static final Block thatchSlope = new BlockSlope(BLBlockRegistry.thatch, 0).setBlockName("thebetweenlands.thatchSlope").setCreativeTab(ModCreativeTabs.blocks);

	public static void registerSlabs(BlockBLSlab slab, String fieldName) {
		try {
			if (!slab.fullBlock) {
				Object obj = BLBlockRegistry.class.getDeclaredField(fieldName + "Double").get(null);
				if (obj instanceof BlockBLSlab)
					GameRegistry.registerBlock(slab, ItemBlockSlab.class, slab.getLocalizedName(), slab, obj, false);
			} else
				GameRegistry.registerBlock(slab, ItemBlockSlab.class, slab.getUnlocalizedName() + "Double", slab.dropsThis, slab, true);
			if (!StatCollector.canTranslate(slab.getUnlocalizedName() + ".name")) {
				TheBetweenlands.unlocalizedNames.add(slab.getUnlocalizedName());
			}
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		initBlocks();
		registerBlocks();
		registerProperties();
	}

	private static void initBlocks() {
	}

	private static void registerBlocks() {
		try {
			for (Field f : BLBlockRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Block && !(obj instanceof BlockBLSlab))
					registerBlock((Block) obj);
				else if (obj instanceof Block[])
					for (Block block : (Block[]) obj)
						registerBlock(block);
				else if (obj instanceof BlockBLSlab)
					registerSlabs((BlockBLSlab) obj, f.getName());
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void registerBlock(Block block) {
		BLOCKS.add(block);
		String name = block.getUnlocalizedName();
		String[] strings = name.split("\\.");

		if (block instanceof ISubBlocksBlock)
			GameRegistry.registerBlock(block, ((ISubBlocksBlock) block).getItemBlockClass(), strings[strings.length - 1]);
		else
			GameRegistry.registerBlock(block, strings[strings.length - 1]);

		if (!StatCollector.canTranslate(block.getUnlocalizedName()  + ".name")) {
			TheBetweenlands.unlocalizedNames.add(block.getUnlocalizedName() + ".name");
		}
	}

	private static void registerProperties() {
		//for fire etc
		Blocks.fire.setFireInfo(weedwoodPlanks, 5, 20);
		Blocks.fire.setFireInfo(rubberTreePlanks, 5, 20);
		Blocks.fire.setFireInfo(purpleRainPlanks, 5, 20);

		weedwoodPlanks.setHarvestLevel("axe", 0);
		rubberTreePlanks.setHarvestLevel("axe", 0);
		purpleRainPlanks.setHarvestLevel("axe", 0);

		aquaMiddleGemOre.setHarvestLevel("shovel", 0);
		crimsonMiddleGemOre.setHarvestLevel("shovel", 0);
		greenMiddleGemOre.setHarvestLevel("shovel", 0);
		octineOre.setHarvestLevel("pickaxe", 1);
		sulfurOre.setHarvestLevel("pickaxe", 0);
		boneOre.setHarvestLevel("pickaxe", 0);
		valoniteOre.setHarvestLevel("pickaxe", 2);
		lifeCrystalOre.setHarvestLevel("pickaxe", 0);
	}

	public static interface ISubBlocksBlock {
		Class<? extends ItemBlock> getItemBlockClass();
	}
}
