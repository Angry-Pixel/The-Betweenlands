package thebetweenlands.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;

public class BlockLeavesBetweenlands extends BlockLeaves implements IStateMappedBlock {
	private int[] decayBlockCache;

	public BlockLeavesBetweenlands() {
		setHardness(0.2F);
		setLightOpacity(1);
		setCreativeTab(BLCreativeTabs.PLANTS);
		setSoundType(SoundType.PLANT);
		setDefaultState(blockState.getBaseState().withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true));
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return !Minecraft.getMinecraft().gameSettings.fancyGraphics && blockAccess.getBlockState(pos.offset(side)).getBlock() == this ? false : true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return !Minecraft.getMinecraft().gameSettings.fancyGraphics;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return Blocks.LEAVES.getBlockLayer();
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(new ItemStack(this));
		return ret;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		if (!state.getValue(DECAYABLE).booleanValue())
			i |= 4;
		if (state.getValue(CHECK_DECAY).booleanValue())
			i |= 8;

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { CHECK_DECAY, DECAYABLE });
	}

	@Override
	public EnumType getWoodType(int meta) {
		return EnumType.OAK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(world.rand.nextInt(200) == 0) {
			if(world.isAirBlock(pos.down())) {
				//	BLParticle.LEAF.spawn(world, pos.add(rand.nextFloat(), 0, rand.nextFloat()));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(StateMap.Builder builder) {
		builder.ignore(BlockLeavesBetweenlands.CHECK_DECAY, BlockLeavesBetweenlands.DECAYABLE);		
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			if (((Boolean)state.getValue(CHECK_DECAY)).booleanValue() && ((Boolean)state.getValue(DECAYABLE)).booleanValue()) {
				byte logReach = 5;
				int checkRadius = logReach + 1;
				byte cacheSize = 32;
				int cacheSquared = cacheSize * cacheSize;
				int cacheHalf = cacheSize / 2;

				if (this.decayBlockCache == null) {
					this.decayBlockCache = new int[cacheSize * cacheSize * cacheSize];
				}

				//states:
				//0: can sustain leaves
				//-1: can't sustain leaves
				//-2: is leaves block

				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();

				if (worldIn.isAreaLoaded(new BlockPos(x - checkRadius, y - checkRadius, z - checkRadius), new BlockPos(x + checkRadius, y + checkRadius, z + checkRadius))) {
					BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

					//Pupulate block cache
					for (int xo = -logReach; xo <= logReach; ++xo) {
						for (int yo = -logReach; yo <= logReach; ++yo) {
							for (int zo = -logReach; zo <= logReach; ++zo) {
								IBlockState blockState = worldIn.getBlockState(mutableBlockPos.setPos(x + xo, y + yo, z + zo));
								Block block = blockState.getBlock();

								if (!block.canSustainLeaves(blockState, worldIn, mutableBlockPos.setPos(x + xo, y + yo, z + zo))) {
									if (block.isLeaves(blockState, worldIn, mutableBlockPos.setPos(x + xo, y + yo, z + zo))) {
										this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = -2;
									} else {
										this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = -1;
									}
								} else {
									this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = 0;
								}
							}
						}
					}

					//Iterate multiple times over the block cache
					for (int distancePass = 1; distancePass <= logReach; ++distancePass) {
						for (int xo = -logReach; xo <= logReach; ++xo) {
							for (int yo = -logReach; yo <= logReach; ++yo) {
								for (int zo = -logReach; zo <= logReach; ++zo) {
									//If value != distancePass - 1 then it's not connected to a log
									if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] == distancePass - 1) {
										//Check for adjacent leaves and set their value to the current distance pass

										if (this.decayBlockCache[(xo + cacheHalf - 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf - 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf + 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf + 1) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf - 1) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf - 1) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf + 1) * cacheSize + zo + cacheHalf] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf + 1) * cacheSize + zo + cacheHalf] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + (zo + cacheHalf - 1)] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + (zo + cacheHalf - 1)] = distancePass;
										}

										if (this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf + 1] == -2) {
											this.decayBlockCache[(xo + cacheHalf) * cacheSquared + (yo + cacheHalf) * cacheSize + zo + cacheHalf + 1] = distancePass;
										}
									}
								}
							}
						}
					}
				}

				//Get distance to log at center block
				int distanceToLog = this.decayBlockCache[cacheHalf * cacheSquared + cacheHalf * cacheSize + cacheHalf];

				if (distanceToLog >= 0) {
					worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, Boolean.valueOf(false)), 4);
				} else {
					this.removeLeaves(worldIn, pos);
				}
			}
		}
	}

	protected void removeLeaves(World world, BlockPos pos) {
		this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
		world.setBlockToAir(pos);
	}
}