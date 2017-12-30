package thebetweenlands.common.block.plant;

import static net.minecraft.block.BlockLog.LOG_AXIS;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockBulbCappedMushroomStalk extends BasicBlock {
	public static final PropertyBool GROUND = PropertyBool.create("ground");

	public BlockBulbCappedMushroomStalk() {
		super(Material.WOOD);
		setSoundType(SoundType.CLOTH);
		setHardness(0.2F);
		setLightLevel(1.0F);
		setDefaultState(blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y).withProperty(GROUND, false));
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (!worldIn.isRemote) {
			int dropChance = 6;

			if (fortune > 0) {
				dropChance -= 2 * fortune;
				if (dropChance < 1) {
					dropChance = 1;
				}
			}
			
			if (dropChance <= 1 || worldIn.rand.nextInt(dropChance) == 0) {
				spawnAsEntity(worldIn, pos, new ItemStack(ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM));
			}
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = getDefaultState();
		if (meta % 2 == 0)
			state = state.withProperty(GROUND, true);
		switch (meta) {
		case 0:
		case 1:
			state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
			break;
		case 8:
		case 9:
			state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
			break;
		case 4:
		case 5:
			state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
			break;
		default:
			state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
			break;
		}

		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = state.getValue(GROUND) ? 1 : 0;
		switch (state.getValue(LOG_AXIS)) {
		case X:
			return meta + 4;
		case Z:
			return meta + 8;
		case NONE:
			return meta + 12;
		default:
			return meta;
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LOG_AXIS, GROUND);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getStateFromMeta(meta).withProperty(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis())).withProperty(GROUND, false);
	}
}
