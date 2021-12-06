package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.entity.mobs.EntitySwarm;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockWeedwoodBushInfested extends BlockWeedwoodBush {
	private ItemStack drop;
	private int stage; //dunno yet

	public BlockWeedwoodBushInfested(ItemStack drop, int stage) {
		this.drop = drop;
		this.stage = stage;
		setTickRandomly(true);
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return this.drop.getItem();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.drop.getItemDamage();
	}

	@Override
	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing dir) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return block instanceof BlockWeedwoodBushInfested;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote)
			return;
		switch (stage) {
		case 0:
			world.setBlockState(pos, BlockRegistry.WEEDWOOD_BUSH_INFESTED_1.getDefaultState(), 2);
			break;
		case 1:
				world.setBlockState(pos, BlockRegistry.WEEDWOOD_BUSH_INFESTED_2.getDefaultState(), 2);
			break;
		case 2:
				world.setBlockState(pos, BlockRegistry.WEEDWOOD_BUSH_INFESTED_3.getDefaultState(), 2);
			break;
		case 3:
				world.setBlockState(pos, BlockRegistry.WEEDWOOD_BUSH_INFESTED_4.getDefaultState(), 2);
			break;
		case 4:
				world.setBlockState(pos, BlockRegistry.DEAD_WEEDWOOD_BUSH.getDefaultState(), 2);
				EntitySwarm swarm = new EntitySwarm(world);
				swarm.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				world.spawnEntity(swarm);
			break;
			
		}
	}

	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItem(hand);
		if (!world.isRemote && !held.isEmpty() && (held.getItem() instanceof ItemShears || held.getItem() == ItemRegistry.SILT_CRAB_CLAW)) {
			IBlockState iblockstate = world.getBlockState(pos);
			Block block = iblockstate.getBlock();
			if (block == BlockRegistry.WEEDWOOD_BUSH_INFESTED_2 || block == BlockRegistry.WEEDWOOD_BUSH_INFESTED_3) {
				ItemStack harvest = new ItemStack(drop.getItem(), 2, drop.getItemDamage());
				EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, harvest);
				item.motionX = item.motionY = item.motionZ = 0D;
				world.spawnEntity(item);
				world.setBlockState(pos, BlockRegistry.WEEDWOOD_BUSH.getDefaultState(), 2);
				held.damageItem(1, player);
				return true;
			}
		}
		return true;
    }

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() instanceof ItemShears || item.getItem() == ItemRegistry.SILT_CRAB_CLAW;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(BlockRegistry.WEEDWOOD_BUSH.getDefaultState().getBlock())), new ItemStack(drop.getItem(), 1, drop.getItemDamage()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		double px = (double) pos.getX() + 0.5D;
		double py = (double) pos.getY() + 1.2D;
		double pz = (double) pos.getZ() + 0.5D;
		if (world.rand.nextInt(5) == 0) {
			switch (stage) {
			case 0:
				BLParticles.SULFUR_TORCH.spawn(world, px, py, pz);
				break;
			case 1:
				BLParticles.MOTH.spawn(world, px, py, pz);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				BLParticles.DIRT_DECAY.spawn(world, px, py, pz);
				break;
			}
		}
	}

	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return 0xFFFFFFFF;
	}
	
	@Override
	public boolean isFarmable(World world, BlockPos pos, IBlockState state) {
		return false;
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		return false;
	}
	
	@Override
	public float getSpreadChance(World world, BlockPos pos, IBlockState state, BlockPos taretPos, Random rand) {
		return 0F;
	}

	@Override
	public void spreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
	}

	@Override
	public void decayPlant(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockToAir(pos);
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, IBlockState state, Random rand) {
		return 0;
	}
}
