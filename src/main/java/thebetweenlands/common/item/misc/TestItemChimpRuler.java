package thebetweenlands.common.item.misc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSludgeWormDungeon;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;

//MINE!!
public class TestItemChimpRuler extends Item {
	public TestItemChimpRuler() {
		this.setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flag) {
		if (hasTag(stack) && stack.getTagCompound().hasKey("homeX")) {
			list.add(TextFormatting.YELLOW + new TextComponentTranslation("tooltip.bl.chimp_ruler.homex", stack.getTagCompound().getInteger("homeX")).getFormattedText());
			list.add(TextFormatting.YELLOW + new TextComponentTranslation("tooltip.bl.chimp_ruler.homey", stack.getTagCompound().getInteger("homeY")).getFormattedText());
			list.add(TextFormatting.YELLOW + new TextComponentTranslation("tooltip.bl.chimp_ruler.homez", stack.getTagCompound().getInteger("homeZ")).getFormattedText());
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (hasTag(stack) && player.isSneaking() && hand.equals(EnumHand.MAIN_HAND)) {
			Block block = world.getBlockState(pos).getBlock();
			if (!world.isRemote && block != null) {
				stack.getTagCompound().setInteger("homeX", pos.getX());
				stack.getTagCompound().setInteger("homeY", pos.getY());
				stack.getTagCompound().setInteger("homeZ", pos.getZ());
				return EnumActionResult.SUCCESS;
			}
		}

		if (hasTag(stack) && stack.getTagCompound().hasKey("homeX") && !player.isSneaking() && hand.equals(EnumHand.MAIN_HAND)) {
			IBlockState state = world.getBlockState(pos);
			if (!world.isRemote && state.getBlock() != null) {
				int x = pos.getX() - stack.getTagCompound().getInteger("homeX");
				int y = pos.getY() - stack.getTagCompound().getInteger("homeY");
				int z = pos.getZ() - stack.getTagCompound().getInteger("homeZ");
				player.sendStatusMessage(new TextComponentTranslation("chat.chimp_ruler_x", x), false);
				player.sendStatusMessage(new TextComponentTranslation("chat.chimp_ruler_y", y), false);
				player.sendStatusMessage(new TextComponentTranslation("chat.chimp_ruler_z", z), false);
			//	String[] name = state.getBlock().getRegistryName().toString().toUpperCase().split(":");
			//	CopytoClipboard("rotatedCubeVolume(world, rand, pos, " + x + ", "+ y + ", " + z + ", blockHelper." + name[1] + ", 1, 1, 1, facing);");
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(!worldIn.isRemote && handIn == EnumHand.OFF_HAND) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			int x = stack.getTagCompound().getInteger("homeX");
			int y = stack.getTagCompound().getInteger("homeY");
			int z = stack.getTagCompound().getInteger("homeZ");
			BlockPos home = new BlockPos(x, y, z);
			this.doUseAction(worldIn, playerIn, home);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	protected void doUseAction(World world, EntityPlayer player, BlockPos home) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
		ILocalStorageHandler handler = worldStorage.getLocalStorageHandler();
		handler.getLocalStorages(LocationStorage.class, new AxisAlignedBB(home.add(-80, -80, -80), home.add(80, 80, 80)), l -> true).forEach(l -> handler.removeLocalStorage(l));
		new WorldGenSludgeWormDungeon().generateLocations(world, world.rand, home);
	}

	public static boolean CopytoClipboard(String string) {
		try {
			StringSelection selection = new StringSelection(string);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			return true;
		} catch (Exception e) {
			System.out.println("Poo-poo, pee-pee bum.");
			return false;
		}
	}

	private boolean hasTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return false;
		}
		return true;
	}
	
	@Override
	public CreativeTabs getCreativeTab() {
		return BetweenlandsConfig.DEBUG.debug ? BLCreativeTabs.SPECIALS : null;
	}
}
