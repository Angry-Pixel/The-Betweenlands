package thebetweenlands.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class BlockMossBed extends BlockBed {

	public IIcon bedIcon;

	public BlockMossBed() {
		super();
		setBlockName("thebetweenlands.mossBed");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
		if (world.isRemote) {
			return true;
		} else if (player.dimension == ConfigHandler.DIMENSION_ID) {
			player.setSpawnChunk(new ChunkCoordinates(x, y, z), false, ConfigHandler.DIMENSION_ID);
			player.addChatMessage(new ChatComponentTranslation("chat.bedSpawnSet"));
			return true;
		}
		return true;
	}

	@Override
	public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player) {
		return true;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int count) {
		return BLItemRegistry.mossBed;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.bedIcon = iconRegister.registerIcon("thebetweenlands:mossBed");
	}

	@Override
	public int getRenderType() {
		return ClientProxy.BlockRenderIDs.MOSS_BED.id();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return this.bedIcon;
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(this);
	}
}
