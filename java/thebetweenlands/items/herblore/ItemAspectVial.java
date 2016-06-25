package thebetweenlands.items.herblore;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.event.render.AspectItemOverlayHandler;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityAspectVial;
import thebetweenlands.utils.AdvancedRecipeHelper;
import thebetweenlands.utils.AtlasIcon;
import thebetweenlands.utils.ColorUtils;

public class ItemAspectVial extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon iconLiquid, iconVialOrange;

	@SideOnly(Side.CLIENT)
	private IIcon[] aspectIcons;

	public ItemAspectVial() {
		this.setUnlocalizedName("item.thebetweenlands.aspectVial");

		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);

		this.setTextureName("thebetweenlands:strictlyHerblore/misc/vialGreen");

		this.setContainerItem(BLItemRegistry.dentrothystVial);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		List<Aspect> itemAspects = AspectManager.getDynamicAspects(stack);
		if(itemAspects.size() >= 1) {
			Aspect aspect = itemAspects.get(0);
			return super.getItemStackDisplayName(stack) + " - " + aspect.type.getName() + " (" + AspectItemOverlayHandler.ASPECT_AMOUNT_FORMAT.format(aspect.getAmount()) + ")";
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.iconLiquid = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/aspectLiquid");
		this.iconVialOrange = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialOrange");
		IIcon aspectIconAtlas = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/aspectMap");
		this.aspectIcons = new IIcon[AspectRegistry.ASPECT_TYPES.size()];
		for(IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
			this.aspectIcons[aspect.getIconIndex()] = new AtlasIcon(aspectIconAtlas, aspect.getIconIndex(), 4);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		switch(pass){
		case 0:
			//Liquid
			List<Aspect> aspects = AspectManager.getDynamicAspects(stack);
			if(aspects.size() > 0) {
				Aspect aspect = aspects.get(0);
				float[] aspectRGBA = ColorUtils.getRGBA(aspect.type.getColor());
				return ColorUtils.toHex(aspectRGBA[0], aspectRGBA[1], aspectRGBA[2], 1.0F);
			}
			return 0xFFFFFFFF;
		case 2:
			return 0xFFFFFFFF;
		}
		return 0xFFFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
		return pass == 0 ? this.iconLiquid : super.getIconFromDamageForRenderPass(damage, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return damage % 2 == 0 ? this.itemIcon : this.iconVialOrange;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		if(pass == 2) {
			List<Aspect> itemAspects = AspectManager.getDynamicAspects(stack);
			if(itemAspects.size() >= 1) {
				Aspect aspect = itemAspects.get(0);
				return this.aspectIcons[aspect.type.getIconIndex()];
			}
		}
		return super.getIcon(stack, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0)); //green
		list.add(new ItemStack(item, 1, 1)); //orange

		//Add all aspects
		for(IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
			Aspect itemAspect = new Aspect(aspect, 4.0F);
			ItemStack stackGreen = new ItemStack(item, 1, 0);
			AspectManager.addDynamicAspects(stackGreen, itemAspect);
			list.add(stackGreen);
			ItemStack stackOrange = new ItemStack(item, 1, 1);
			AspectManager.addDynamicAspects(stackOrange, itemAspect);
			list.add(stackOrange);
		}
	}


	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderPasses(int metadata) {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 3 : 2;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			switch(stack.getItemDamage()) {
			case 0:
				return "item.thebetweenlands.aspectVial.green";
			case 1:
				return "item.thebetweenlands.aspectVial.orange";
			}
		} catch (Exception e) { }
		return "item.thebetweenlands.unknown";
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		ItemStack containerDefault;
		switch(itemStack.getItemDamage()) {
		default:
		case 0:
			containerDefault = BLItemRegistry.dentrothystVial.createStack(0);
			break;
		case 1:
			containerDefault = BLItemRegistry.dentrothystVial.createStack(2);
			break;
		}
		ItemStack containerRubberBoots = AdvancedRecipeHelper.getContainerItem(itemStack, null, "rubberBoots");
		ItemStack containerBait = AdvancedRecipeHelper.getContainerItem(itemStack, null, "bait");
		return containerRubberBoots != null ? containerRubberBoots : (containerBait != null ? containerBait : containerDefault);
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List lst, boolean debug) {
		if(!AspectManager.getDynamicAspects(stack).isEmpty() && AspectManager.getDynamicAspects(stack).get(0).type == AspectRegistry.BYARIIS) {
			lst.add(StatCollector.translateToLocal("aspectvial.byariis.fuel"));
		}
	}

	/**
	 * Places an aspect vial with the specified aspects in it (can be null)
	 * @param x
	 * @param y
	 * @param z
	 * @param vialType Vial type: 0: green, 1: orange
	 * @param aspect
	 */
	public static void placeAspectVial(World world, int x, int y, int z, int vialType, Aspect aspect) {
		world.setBlock(x, y, z, BLBlockRegistry.vial, vialType, 2);
		TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(x, y, z);
		if(tile != null)
			tile.setAspect(aspect);
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return world.getBlock(x, y, z) == BLBlockRegistry.vial;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(player.isSneaking() && AspectManager.getDynamicAspects(stack).size() == 1 && side == 1) {
			if(world.isAirBlock(x, y + 1, z)) {
				if(!world.isRemote) {
					ItemAspectVial.placeAspectVial(world, x, y + 1, z, stack.getItemDamage(), AspectManager.getDynamicAspects(stack).get(0));
					stack.stackSize--;
				}
				return true;
			}
		}
		return false;
	}
}
