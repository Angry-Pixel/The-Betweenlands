package thebetweenlands.common.block.misc;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap.Builder;
import thebetweenlands.util.config.ConfigHandler;

public class BlockMossBed extends BlockBed implements IStateMappedBlock, ICustomItemBlock {
	public BlockMossBed() { 
		this.setCreativeTab(null);
		this.setSoundType(SoundType.WOOD);
		this.setHardness(0.2F);
		this.disableStats();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : ItemRegistry.MOSS_BED_ITEM;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ItemRegistry.MOSS_BED_ITEM);
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(OCCUPIED);
	}

	@Override
	public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, Entity player) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.dimension == ConfigHandler.dimensionId) {
			if(!world.isRemote) {
				player.setSpawnPoint(pos, false);
				player.addChatMessage(new TextComponentTranslation("chat.bedSpawnSet"));
			}
			return true;
		} else {
			return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getRenderedItem() {
		return ItemRegistry.MOSS_BED_ITEM;
	}
}