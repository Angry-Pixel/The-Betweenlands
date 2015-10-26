package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.herblore.elixirs.effects.ElixirRegistry;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockPoisonIvy extends BlockVine implements IShearable {
	private final int[] directionToMeta = { -1, -1, 1, 4, 8, 2 };

	@SideOnly(Side.CLIENT)
	private IIcon spoopyIcon;

	public BlockPoisonIvy() {
		super();
		setHardness(0.2F);
		setBlockName("thebetweenlands.poisonIvy");
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		setBlockTextureName("thebetweenlands:poisonIvy");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(!world.isRemote && entity instanceof EntityLivingBase && world.rand.nextInt(200) == 0 && !ElixirRegistry.EFFECT_TOUGHSKIN.isActive((EntityLivingBase)entity)){
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.getId(), 50, 25));			
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		spoopyIcon = reg.registerIcon(this.getTextureName() + "Spoopy");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld)) {
			return this.spoopyIcon;
		}
		return super.getIcon(side, meta);
	}

	public int getMetaForDirection(ForgeDirection direction) {
		return directionToMeta[direction.ordinal()];
	}
}