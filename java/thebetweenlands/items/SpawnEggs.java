package thebetweenlands.items;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SpawnEggs extends ItemMonsterPlacer {
	private static final Map<Short, EggData> eggTypes = new LinkedHashMap<Short, EggData>();

	public static final void registerSpawnEgg(Class<? extends EntityLiving> entity, String entityName, int id, int eggBackgroundColor, int eggForegroundColor) {
		eggTypes.put((short) id, new EggData(id, entityName, entity, eggBackgroundColor, eggForegroundColor));
	}

	private static final EggData getEggData(ItemStack is) {
		return eggTypes.get((short) is.getItemDamage());
	}

	public SpawnEggs() {
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		String s = StatCollector.translateToLocal(getUnlocalizedName() + ".name").trim();
		String mob = "";

		EggData egg = getEggData(is);
		if (egg != null)
			mob = StatCollector.translateToLocal("entity." +  ModInfo.ID + "." + egg.entityName + ".name");

		return String.format(s, mob);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;

		Block block = world.getBlock(x, y, z);
		x += Facing.offsetsXForSide[side];
		y += Facing.offsetsYForSide[side];
		z += Facing.offsetsZForSide[side];

		EggData egg = getEggData(is);
		if (egg != null) {
			egg.spawnMob(world, x + 0.5D, y + (side == 1 && block != null && block.getRenderType() == 11 ? 0.5D : 0D), z + 0.5D, is);

			if (!player.capabilities.isCreativeMode)
				--is.stackSize;
		}

		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
		if (world.isRemote)
			return is;

		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);

		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			int x = mop.blockX, y = mop.blockY, z = mop.blockZ;

			if (!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, mop.sideHit, is))
				return is;

			if (world.getBlock(x, y, z).getMaterial() == Material.water) {
				EggData egg = getEggData(is);
				if (egg != null) {
					egg.spawnMob(world, x, y, z, is);

					if (!player.capabilities.isCreativeMode)
						--is.stackSize;
				}
			}
		}

		return is;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass) {
		EggData egg = getEggData(is);
		return egg != null ? pass == 0 ? egg.primaryColor : egg.secondaryColor : 16777215;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		for (Short s : eggTypes.keySet())
			list.add(new ItemStack(id, 1, s));
	}

	static class EggData {

		private final short id;
		String entityName;
		private final Class<? extends EntityLiving> entityClass;
		int primaryColor;
		int secondaryColor;

		EggData(int id, String entityName, Class<? extends EntityLiving> entityClass, int[] rgbPrimaryColor, int[] rgbSecondaryColor) {
			this(id, entityName, entityClass, rgbPrimaryColor[0] << 16 | rgbPrimaryColor[1] << 8 | rgbPrimaryColor[2], rgbSecondaryColor[0] << 16 | rgbSecondaryColor[1] << 8 | rgbSecondaryColor[2]);
		}

		EggData(int id, String entityName, Class<? extends EntityLiving> entityClass, int primaryColor, int secondaryColor) {
			this.id = (short) id;
			this.entityName = entityName;
			this.entityClass = entityClass;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
		}

		public EntityLiving spawnMob(World world, double x, double y, double z, ItemStack is) {
			EntityLiving e = null;

			try {
				e = entityClass.getConstructor(World.class).newInstance(world);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

			if (e == null)
				return null;

			e.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360F), 0F);
			e.rotationYawHead = e.rotationYaw;
			e.renderYawOffset = e.rotationYaw;
			e.onSpawnWithEgg((IEntityLivingData) null);
			world.spawnEntityInWorld(e);
			e.playLivingSound();

			if (is.hasDisplayName())
				e.setCustomNameTag(is.getDisplayName());

			return e;
		}

		@Override
		public int hashCode() {
			return id;
		}
	}
}