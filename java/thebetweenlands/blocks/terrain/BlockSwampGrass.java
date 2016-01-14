package thebetweenlands.blocks.terrain;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockSwampGrass
extends Block implements IGrowable {
	@SideOnly(Side.CLIENT)
	private IIcon topIcon, sideIcon, spoopyTopIcon, spoopySideIcon;

	public BlockSwampGrass() {
		super(Material.grass);
		setHardness(0.5F);
		setStepSound(soundTypeGrass);
		setHarvestLevel("shovel", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.swampGrass");
		setTickRandomly(true);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemSpadeBL) {
			if(!world.isRemote) {
				Block farmedDirt = BLBlockRegistry.farmedDirt;
				world.setBlock(x, y, z, BLBlockRegistry.farmedDirt, 2, 3);
				world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), farmedDirt.stepSound.getStepResourcePath(), (farmedDirt.stepSound.getVolume() + 1.0F) / 2.0F, farmedDirt.stepSound.getPitch() * 0.8F);
				world.playAuxSFXAtEntity(null, 2001, x, y + 1, z, Block.getIdFromBlock(world.getBlock(x, y, z)));
				player.getCurrentEquippedItem().damageItem(1, player);
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld)) {
			if (side == 2 || side == 3 || side == 4 || side == 5) {
				return this.spoopySideIcon;
			} else if (side == 1) {
				return this.spoopyTopIcon;
			}
		} else {
			if (side == 2 || side == 3 || side == 4 || side == 5) {
				return this.sideIcon;
			} else if (side == 1) {
				return this.topIcon;
			}
		}

		return this.blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon("thebetweenlands:swampDirt");
		this.sideIcon = reg.registerIcon("thebetweenlands:swampGrassSide");
		this.topIcon = reg.registerIcon("thebetweenlands:swampGrassTop");
		this.spoopySideIcon = reg.registerIcon("thebetweenlands:swampGrassSideSpoopy");
		this.spoopyTopIcon = reg.registerIcon("thebetweenlands:swampGrassTopSpoopy");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!world.isRemote) {
			if (world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2) {
				world.setBlock(x, y, z, BLBlockRegistry.swampDirt);
			} else if (world.getBlockLightValue(x, y + 1, z) >= 9) {
				for (int l = 0; l < 4; ++l) {
					int xTarget = x + rand.nextInt(3) - 1;
					int yTarget = y + rand.nextInt(5) - 3;
					int zTarget = z + rand.nextInt(3) - 1;
					Block block = world.getBlock(xTarget, yTarget + 1, zTarget);

					if (world.getBlock(xTarget, yTarget, zTarget) == Blocks.dirt
							&& world.getBlockMetadata(xTarget, yTarget, zTarget) == 0
							&& world.getBlockLightValue(xTarget, yTarget + 1, zTarget) >= 4
							&& world.getBlockLightOpacity(xTarget, yTarget + 1, zTarget) <= 2) {
						world.setBlock(xTarget, yTarget, zTarget, BLBlockRegistry.swampGrass);
					}
				}
			}
		}
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return BLBlockRegistry.swampDirt.getItemDropped(0, rand, fortune);
	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean p_149851_5_) {
		return true;
	}

	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
		return true;
	}

	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z) {
		int l = 0;

		while (l < 128) {
			int i1 = x;
			int j1 = y + 1;
			int k1 = z;
			int l1 = 0;

			while (true) {
				if (l1 < l / 16) {
					i1 += rand.nextInt(3) - 1;
					j1 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
					k1 += rand.nextInt(3) - 1;

					if (world.getBlock(i1, j1 - 1, k1) == BLBlockRegistry.swampGrass && !world.getBlock(i1, j1, k1).isNormalCube()) {
						++l1;
						continue;
					}
				} else if (world.getBlock(i1, j1, k1) == Blocks.air) {
					if (rand.nextInt(6) != 0) {
						if (BLBlockRegistry.swampTallGrass.canBlockStay(world, i1, j1, k1)) {
							world.setBlock(i1, j1, k1, BLBlockRegistry.swampTallGrass, 1, 3);
						}
					} else {
						world.getBiomeGenForCoords(i1, k1).plantFlower(world, rand, i1, j1, k1);
					}
				}

				++l;
				break;
			}
		}
	}


}
