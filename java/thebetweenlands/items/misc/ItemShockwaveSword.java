package thebetweenlands.items.misc;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.entities.EntityShockwaveBlock;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.food.ItemForbiddenFig;
import thebetweenlands.items.tools.ItemSwordBL;
import thebetweenlands.utils.CorrodibleItemHelper;

public class ItemShockwaveSword extends ItemSwordBL {

	@SideOnly(Side.CLIENT)
	private IIcon iconCharging;

	@SideOnly(Side.CLIENT)
	private IIcon[] iconsChargingCorroded;

	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
		setUnlocalizedName("thebetweenlands.shockwaveSword");
		setTextureName("thebetweenlands:shockwaveSword");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		ItemShockwaveSword item = (ItemShockwaveSword) stack.getItem();
		if (item == BLItemRegistry.shockwaveSword)
			list.add(StatCollector.translateToLocal("shockwaveSword.usage"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.iconCharging = reg.registerIcon("thebetweenlands:shockwaveSwordDepleted");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconIndex(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.stackTagCompound = new NBTTagCompound();
		if (!stack.getTagCompound().hasKey("uses"))
			stack.getTagCompound().setInteger("uses", 0);
		if(stack.getTagCompound().getInteger("uses") == 3)
			return this.iconsChargingCorroded[CorrodibleItemHelper.getCorrosionStage(stack)];
		return super.getIconIndex(stack);
	}

	@Override
	public IIcon[] getIcons() {
		IIcon[] defaultIcons = super.getIcons();
		IIcon[] allIcons = new IIcon[defaultIcons.length + 1];
		System.arraycopy(defaultIcons, 0, allIcons, 0, defaultIcons.length);
		allIcons[allIcons.length - 1] = this.iconCharging;
		return allIcons;
	}

	@Override
	public void setCorrosionIcons(IIcon[][] corrosionIcons) {
		super.setCorrosionIcons(corrosionIcons);
		this.iconsChargingCorroded = corrosionIcons[corrosionIcons.length - 1];
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
		if (!stack.hasTagCompound())
			stack.stackTagCompound = new NBTTagCompound();
		if (!stack.getTagCompound().hasKey("cooldown"))
			stack.getTagCompound().setInteger("cooldown", 0);
		if (!stack.getTagCompound().hasKey("uses"))
			stack.getTagCompound().setInteger("uses", 0);

		if(stack.getTagCompound().getInteger("uses") == 3) {
			if (stack.getTagCompound().getInteger("cooldown") < 60)
				stack.getTagCompound().setInteger("cooldown", stack.getTagCompound().getInteger("cooldown") + 1);
			if (stack.getTagCompound().getInteger("cooldown") >= 60) {
				stack.getTagCompound().setInteger("cooldown", 60);
				stack.getTagCompound().setInteger("uses", 0);
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1000;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!stack.hasTagCompound()) {
			stack.stackTagCompound = new NBTTagCompound();
			return false;
		}
		if (side == 1 && player.isSneaking()) {

			if (!world.isRemote) {
				if (stack.getTagCompound().getInteger("uses") < 3) {
					stack.damageItem(2, player);
					world.playSoundEffect(player.posX, player.posY, player.posZ, "thebetweenlands:shockwaveSword", 1.0F, 2.0F);
					double direction = Math.toRadians(player.rotationYaw);
					Vec3 diag = Vec3.createVectorHelper(Math.sin(direction + Math.PI / 2.0D), 0, Math.cos(direction + Math.PI / 2.0D)).normalize();
					for (int distance = 1; distance < 16; distance++) {
						for(int distance2 = -(distance-2); distance2 < (distance-2); distance2++) {
							int originX = MathHelper.floor_double(player.posX - Math.sin(direction) * distance - diag.xCoord * distance2 * 0.25D);
							int originY = y;
							int originZ = MathHelper.floor_double(player.posZ + Math.cos(direction) * distance + diag.zCoord * distance2 * 0.25D);

							Block block = world.getBlock(originX, originY, originZ);

							if (block != null && block.isNormalCube() && !block.hasTileEntity(world.getBlockMetadata(originX, originY, originZ)) 
									&& block.getBlockHardness(world, originX, originY, originZ) <= 5.0F && block.getBlockHardness(world, originX, originY, originZ) >= 0.0F
									&& (world.isAirBlock(originX, originY+1, originZ) || world.getBlock(originX, originY+1, originZ).isReplaceable(world, originX, originY+1, originZ))) {
								stack.getTagCompound().setInteger("blockID", Block.getIdFromBlock(world.getBlock(originX, originY, originZ)));
								stack.getTagCompound().setInteger("blockMeta", world.getBlockMetadata(originX, originY, originZ));

								EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
								shockwaveBlock.setOrigin(originX, originY, originZ, MathHelper.floor_double(Math.sqrt(distance*distance+distance2*distance2)), player.posX, player.posZ, player);
								shockwaveBlock.setLocationAndAngles(originX + 0.5D, originY, originZ + 0.5D, 0.0F, 0.0F);
								shockwaveBlock.setBlock(Block.getBlockById(stack.getTagCompound().getInteger("blockID")), stack.getTagCompound().getInteger("blockMeta"));
								world.setBlockToAir(originX, originY, originZ);
								world.spawnEntityInWorld(shockwaveBlock);
							}
						}
					}
					stack.getTagCompound().setInteger("uses", stack.getTagCompound().getInteger("uses") + 1);
					if (stack.getTagCompound().getInteger("uses") >= 3) {
						stack.getTagCompound().setInteger("uses", 3);
						stack.getTagCompound().setInteger("cooldown", 0);
					}
					return true;
				}
			}
		}
		return false;
	}
}
