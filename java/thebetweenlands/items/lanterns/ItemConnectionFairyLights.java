package thebetweenlands.items.lanterns;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import thebetweenlands.connection.ConnectionType;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;

import java.util.List;

public class ItemConnectionFairyLights extends ItemConnection {
	@SideOnly(Side.CLIENT)
	public static IIcon[] icons;

	public ItemConnectionFairyLights() {
		setCreativeTab(null);
		setTextureName(ModInfo.ID + ":fairy_lights");
		setUnlocalizedName("thebetweenlands.light_connection");
	}

	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.FAIRY_LIGHTS;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer holder, List stringLines, boolean currentlyHeld) {
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			if (tagCompound.hasKey("pattern", 9)) {
				NBTTagList tagList = tagCompound.getTagList("pattern", 10);
				int tagCount = tagList.tagCount();
				if (tagCount > 0) {
					stringLines.add(StatCollector.translateToLocal("item.fairy_lights.pattern"));
				}
				for (int i = 0; i < tagCount; i++) {
					NBTTagCompound lightCompound = tagList.getCompoundTagAt(i);
					String localizedLightVariant = StatCollector.translateToLocal(BLItemRegistry.light.getUnlocalizedName() + '.' + LightVariant.getLightVariant(lightCompound.getInteger("light")).getName() + ".name");
					String localizedColor = StatCollector.translateToLocal("item.fireworksCharge." + ItemDye.field_150923_a[lightCompound.getByte("color")]);
					stringLines.add("  " + StatCollector.translateToLocalFormatted("item.fairy_lights.colored_light", localizedColor, localizedLightVariant));
				}
			}
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass == 0) {
			return super.getColorFromItemStack(stack, renderPass);
		}
		if (stack.hasTagCompound()) {
			NBTTagList tagList = stack.getTagCompound().getTagList("pattern", 10);
			if (tagList.tagCount() > 0) {
				return ItemDye.field_150922_c[tagList.getCompoundTagAt((renderPass - 1) % tagList.tagCount()).getByte("color")];
			}
		}
		return 0xFFD584;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 5;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return icons[pass];
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
		icons = new IIcon[5];
		for (int i = 0; i < icons.length; i++) {
			icons[i] = iconRegister.registerIcon(ModInfo.ID + ":fairy_lights_" + i);
		}
	}
}
