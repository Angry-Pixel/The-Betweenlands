package thebetweenlands.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ToolDecayImage {	
	static ResourceLocation decayResource = new ResourceLocation(ModInfo.ID, "textures/items/toolDecay.png");
	static Random rand = new Random();
	static IIconRegister iconRegister;
	static IIcon iicon;
	static String loc = TheBetweenlands.dir + File.separator + ModInfo.ID + "_images";
	
	public static void createTextureAfterObjects() throws IOException
	{
		new File(loc).mkdirs();
		createTextures(new ItemStack(Items.apple), 33);
		createTextures(new ItemStack(Items.string), 66);
		for(int i = 1; i < new ItemStack(BLItemRegistry.octineChestplate).getMaxDamage(); i++)
			createTextures(new ItemStack(BLItemRegistry.octineChestplate), i/33*33);
		createTextures(new ItemStack(BLItemRegistry.weedwoodSword), 150);
	}
	
	@SideOnly(Side.CLIENT)
	public static void createTextures(ItemStack stack, int alpha) throws IOException {
		IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		ResourceLocation ingot;
		BufferedImage icon, overlay, decay = ImageIO.read(rm.getResource(decayResource).getInputStream());
		File image = new File(loc + File.separator + stack.getDisplayName().toLowerCase().replace(" ", "_")+"_decayed_"+alpha+".png");
		if (!image.exists() && stack != null && Item.getIdFromItem(stack.getItem()) > 0 && stack.getIconIndex() != null) {
			try {
				ingot = getLocation(stack, stack, false);
			} catch (Exception e) {
				ingot = new ResourceLocation("textures/items/apple.png");
			}
			icon = ImageIO.read(rm.getResource(ingot).getInputStream());
			int height = icon.getHeight();
			int width = icon.getWidth();
			overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++) {
					int toolRGB = icon.getRGB(i, j);
					int decayRGB = decay.getRGB(i, j);
					if(rand.nextBoolean()) decayRGB = decayRGB & 0x00ffffff; // make the image transparent.
					else decayRGB = decayRGB & (16777215*(alpha + 1) + alpha); // make the image semi-transparent.
					if(toolRGB>>24 != 0x00) overlay.setRGB(i, j, decayRGB);
					else overlay.setRGB(i, j, toolRGB);
				}
			ImageIO.write(overlay, "png", image);
			System.out.println(image);
		}
	}
	
    public static ResourceLocation getLocation(ItemStack item, ItemStack stack, boolean changeMeta)
    {
        String domain = "";
        String texture;
        IIcon itemIcon = item.getItem().getIcon(item, 0);
        String iconName = itemIcon.getIconName();
        if (iconName.substring(0, iconName.indexOf(":") + 1) != "") domain = iconName.substring(0, iconName.indexOf(":") + 1).replace(":", " ").trim();
        else domain = "minecraft";
        texture = iconName.substring(iconName.lastIndexOf(":") + 1) + ".png";
        ResourceLocation textureLocation = null;
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        if (texturemanager.getResourceLocation(item.getItemSpriteNumber()).toString().contains("items")) textureLocation = new ResourceLocation(domain.toLowerCase(), "textures/items/" + texture);
        else textureLocation = new ResourceLocation(domain.toLowerCase(), "textures/blocks/" + texture);
        return textureLocation;
    }
}
