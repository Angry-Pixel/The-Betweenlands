package thebetweenlands.common.block.farming;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class BlockGenericCrop extends BlockCrops {
	protected Random rand = new Random();

	private int maxHeight = 1;

	/**
	 * Sets the maximum height of this crop
	 * @param maxHeight
	 * @return
	 */
	public BlockGenericCrop setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	/**
	 * Returns the maximum height of this crop
	 * @return
	 */
	public int getMaxHeight() {
		return this.maxHeight;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		ItemStack seedDrop = this.getSeedDrop(world, state, pos);
		ItemStack cropDrop = this.getCropDrop(world, state, pos);
		for(int i = 0; i < this.getSeedDrops(world, state, pos, fortune); i++) {
			ret.add(seedDrop);
		}
		for(int i = 0; i < this.getCropDrops(world, state, pos, fortune); i++) {
			ret.add(cropDrop);
		}
		return ret;
	}

	/**
	 * Returns the amount of seeds to drop
	 * @return
	 */
	public int getSeedDrops(IBlockAccess world, IBlockState state, BlockPos pos, int fortune) {
		return 1 + (this.isMature(world, pos) ? (this.rand.nextInt(3) == 0 ? 1 : 0) + fortune : 0);
	}

	/**
	 * Returns the amount of fruits to drop
	 * @return
	 */
	public int getCropDrops(IBlockAccess world, IBlockState state, BlockPos pos, int fortune) {
		if(this.isMature(world, pos)) {
			return 2 + this.rand.nextInt(3) + fortune;
		}
		return 0;
	}

	/**
	 * Returns the seed item
	 * @return
	 */
	public ItemStack getSeedDrop(IBlockAccess world, IBlockState state, BlockPos pos) {
		return null;	
	}

	/**
	 * Returns the fruit item
	 * @return
	 */
	public ItemStack getCropDrop(IBlockAccess world, IBlockState state, BlockPos pos) {
		return null;	
	}

	/**
	 * Returns true if the crop at the specified position is mature and not decayed
	 * @return
	 */
	public boolean isMature(IBlockAccess world, BlockPos pos) {
		return false; //TODO
	}

	@Override
	protected Item getSeed() {
		return null;
	}

	@Override
	protected Item getCrop() {
		return null;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {

	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		if(this.maxHeight > 1 && state.getBlock() instanceof BlockGenericCrop && this.isSameCrop(state)) {
			return true;
		} else {
			return state.getBlock() instanceof BlockGenericDugSoil && state.getValue(BlockGenericDugSoil.COMPOSTED);
		}
	}

	/**
	 * Returns true if the specified crop should be treated as the same
	 * @return
	 */
	public boolean isSameCrop(IBlockState crop) {
		return crop.getBlock() == this;
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		return (this.maxHeight <= 1 || !this.canGrowTo(world, pos.up()) || this.hasReachedMaxHeight(world, pos)) ? !this.isMature(world, pos): this.isMature(world, pos);
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		int chance = this.isCropOrSoilDecayed(world, pos) ? 6 : 3;
		return rand.nextInt(chance) == 0;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		this.preGrow(world, pos, state);
		/*int l = world.getBlockMetadata(x, y, z) + MathHelper.getRandomIntegerInRange(world.rand, 2, 4);
		if (l > 7) {
			l = 7;
		} TODO
		set state*/
		this.postGrow(world, pos, state, newState);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(!world.isRemote){
			if(this.maxHeight > 1) {
				//Harvest crops above
				if(!player.capabilities.isCreativeMode) {
					for(int yo = 1; yo <= this.maxHeight; yo++) {
						BlockPos offsetPos = pos.add(0, yo, 0);
						IBlockState offsetState = world.getBlockState(offsetPos);
						if(offsetState.getBlock() instanceof BlockGenericCrop && this.isSameCrop(offsetState)) {
							((BlockGenericCrop) offsetState.getBlock()).harvestCrop(world, offsetPos, player);
						} else {
							break;
						}
					}
				}
			}

			//Harvest and destroy current crop
			boolean grown = this.isFullyGrown(world, x, y, z);
			if(!player.capabilities.isCreativeMode) {
				this.harvestCrop(world, pos, player);
			}
			this.destroyCrop(world, pos, state);
			if(grown) {
				BlockPos soilPos = pos.add(pos.down());
				if(!player.capabilities.isCreativeMode && world.getBlockState(soilPos).getBlock() instanceof BlockGenericDugSoil) {
					TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, soilPos);
					if(te != null && te.isComposted()) {
						te.setCompost(te.getCompost() - 1);
					}
				}
			}

			if(this.maxHeight > 1) {
				//Harvest crops below
				if(!player.capabilities.isCreativeMode) {
					for(int yo = 1; yo <= this.maxHeight; yo++) {
						BlockPos offsetPos = pos.add(0, -yo, 0);
						IBlockState offsetState = world.getBlockState(offsetPos);
						if(offsetState.getBlock() instanceof BlockGenericCrop && this.isSameCrop(offsetState)) {
							((BlockGenericCrop) offsetState.getBlock()).harvestCrop(world, offsetPos, player);
						} else {
							break;
						}
					}
				}

				//Destroy crops above and below
				for(int yo = 1; yo <= this.maxHeight; yo++) {
					BlockPos offsetPos = pos.add(0, yo, 0);
					IBlockState offsetState = world.getBlockState(offsetPos);
					if(offsetState.getBlock() instanceof BlockGenericCrop && this.isSameCrop(offsetState)) {
						((BlockGenericCrop) offsetState.getBlock()).destroyCrop(world, offsetPos, offsetState);
					} else {
						break;
					}
				}
				for(int yo = 1; yo <= this.maxHeight; yo++) {
					BlockPos offsetPos = pos.add(0, -yo, 0);
					IBlockState offsetState = world.getBlockState(offsetPos);
					if(offsetState.getBlock() instanceof BlockGenericCrop && this.isSameCrop(offsetState)) {
						((BlockGenericCrop) offsetState.getBlock()).destroyCrop(world, offsetPos, offsetState);

						BlockPos soilPos = pos.add(offsetPos.down());
						if(!player.capabilities.isCreativeMode && world.getBlockState(soilPos).getBlock() instanceof BlockGenericDugSoil) {
							TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, soilPos);
							if(te != null && te.isComposted()) {
								te.setCompost(te.getCompost() - 1);
							}
						}
					} else {
						break;
					}
				}
			}
		}
	}

	/**
	 * Adds block mining stats, exhaustion and drops items
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param player
	 */
	public void harvestCrop(World worldIn, BlockPos pos, EntityPlayer player) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);
		if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) {
			IBlockState state = worldIn.getBlockState(pos);
			int fortune = 0;
			if(player != null && player.getHeldItemMainhand() != null) {
				fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand());
			}
			List<ItemStack> items = this.getDrops(worldIn, pos, state, 0);
			float chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, 1.0F, false, harvesters.get());
			for (ItemStack item : items) {
				if (worldIn.rand.nextFloat() <= chance) {
					spawnAsEntity(worldIn, pos, item);
				}
			}
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		//Don't call super#updateTick, the growing has to be handled in this class only

		this.checkAndDropBlock(world, pos, state);

		if (this.shouldGrow(world, pos)) {
			this.preGrow(world, pos, state);
			if (!this.isFullyGrown(world, pos)) {
				//++meta;
				//world.setBlockMetadataWithNotify(pos, meta, 3);
				//TODO 
			}
			this.postGrow(world, pos, state, newState);
		}
	}

	/**
	 * Returns whether the crop should grow this tick
	 * @return
	 */
	public boolean shouldGrow(World world, BlockPos pos) {
		return world.rand.nextInt(3) == 0;
	}

	/**
	 * Returns whether the soil should decay this tick
	 * @return
	 */
	public boolean shouldDecay(World world, BlockPos pos) {
		return world.rand.nextInt(10) == 0;
	}
}
