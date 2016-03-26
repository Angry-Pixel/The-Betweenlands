package thebetweenlands.items.misc;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.entities.EntityShockwaveBlock;
import thebetweenlands.items.tools.ItemSwordBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShockwaveSword extends ItemSwordBL {

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
        setHasSubtypes(true);
        setUnlocalizedName("thebetweenlands.shockwaveSword");
        setTextureName("thebetweenlands:shockwaveSword");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
		if (!stack.hasTagCompound())
			stack.stackTagCompound = new NBTTagCompound();
		if (!stack.getTagCompound().hasKey("cooldown"))
			stack.getTagCompound().setInteger("cooldown", 0);
		
		if (stack.getTagCompound().getInteger("cooldown") < 30)
			stack.getTagCompound().setInteger("cooldown", stack.getTagCompound().getInteger("cooldown") + 1);
		if (stack.getTagCompound().getInteger("cooldown") >= 30)
			stack.getTagCompound().setInteger("cooldown", 30);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1000;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!stack.hasTagCompound()) {
			stack.stackTagCompound = new NBTTagCompound();
			return false;
		}
		if (side == 1 && player.isSneaking()) {
			if (stack.getTagCompound().getInteger("cooldown") == 30) {
				if (!world.isRemote) {
					double direction = Math.toRadians(player.rotationYaw);
					for(int distance = 2; distance < 16; distance++) {
						int originX = (MathHelper.floor_double(player.posX - Math.sin(direction) * distance));
						int originY = y;
						int originZ	= MathHelper.floor_double(player.posZ + Math.cos(direction) * distance);
						Block block = world.getBlock(originX, originY, originZ);
					
						if (block != null && block.isNormalCube() && !block.hasTileEntity(world.getBlockMetadata(originX, originY, originZ))) {
							world.playSoundEffect(x, y, z, "thebetweenlands:shockwaveSword", 0.125F, 2.0F);
							stack.getTagCompound().setInteger("blockID", Block.getIdFromBlock(world.getBlock(originX, originY, originZ)));
							stack.getTagCompound().setInteger("blockMeta", world.getBlockMetadata(originX, originY, originZ));
						
							EntityShockwaveBlock shockwaveBlock;
							shockwaveBlock = new EntityShockwaveBlock(world);
							shockwaveBlock.setOrigin(originX, originY, originZ, distance);
							shockwaveBlock.setLocationAndAngles(originX + 0.5D, originY, originZ + 0.5D, 0.0F, 0.0F);
							shockwaveBlock.setBlock(Block.getBlockById(stack.getTagCompound().getInteger("blockID")), stack.getTagCompound().getInteger("blockMeta"));
							world.setBlockToAir(originX, originY, originZ);
							world.spawnEntityInWorld(shockwaveBlock);
							stack.getTagCompound().setInteger("cooldown", 0);
						}
					}
					return true;
				}
			}
		}
		return false;
	}
}
