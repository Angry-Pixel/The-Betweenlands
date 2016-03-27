package thebetweenlands.items.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.utils.CorrodibleItemHelper;
import thebetweenlands.utils.IGemTextureProvider;

import java.util.ArrayList;
import java.util.List;

public class ItemSwordBL extends ItemSword implements ICorrodible, IManualEntryItem, IGemTextureProvider {
	private IIcon[] corrosionIcons;
	private String[] gemTextures = new String[CircleGem.TYPES.length];
	private IIcon[][] gemTextureIcons = new IIcon[CircleGem.TYPES.length][1];

	private float attackDamageWeaponModifier;

	public ItemSwordBL(ToolMaterial material) {
		super(material);
		attackDamageWeaponModifier = 4 + material.getDamageVsEntity();
		this.setCreativeTab(null);
	}

	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase entity, EntityLivingBase player) {
		is.damageItem(1, player);
		if (is.getItem() == BLItemRegistry.octineSword)
			if (player.worldObj.rand.nextInt(CircleGem.getGem(is) == CircleGem.CRIMSON ? 3 : 4) == 0)
				entity.setFire(10);
		return true;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		CircleGem gem = CircleGem.getGem(stack);
		if(gem != CircleGem.NONE) {
			int gemID = gem.ordinal();
			if(this.gemTextureIcons[gemID][0] != null) {
				return this.gemTextureIcons[gemID][CorrodibleItemHelper.getCorrosionStage(stack)];
			}
		}
		return corrosionIcons[CorrodibleItemHelper.getCorrosionStage(stack)];
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	@Override
	public IIcon[] getIcons() {
		List<IIcon> iconList = new ArrayList<IIcon>();
		iconList.add(this.itemIcon);
		for(int i = 0; i < this.gemTextureIcons.length; i++) {
			IIcon gemTextureIcon = this.gemTextureIcons[i][0];
			if(gemTextureIcon != null) {
				iconList.add(gemTextureIcon);
			}
		}
		return iconList.toArray(new IIcon[0]);
	}

	@Override
	public void setCorrosionIcons(IIcon[][] corrosionIcons) {
		this.corrosionIcons = corrosionIcons[0];
		for(int i = 0; i < this.gemTextureIcons.length; i++) {
			IIcon gemTextureIcon = this.gemTextureIcons[i][0];
			if(gemTextureIcon != null) {
				this.gemTextureIcons[i] = corrosionIcons[i + 1];
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		for(int i = 0; i < this.gemTextures.length; i++) {
			String gemTexture = this.gemTextures[i];
			if(gemTexture != null) {
				this.gemTextureIcons[i][0] = register.registerIcon(gemTexture);
			}
		}
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrodibleItemHelper.onUpdate(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack itemStack) {
		Multimap multimap = HashMultimap.create();
		AttributeModifier attributeModifier = new AttributeModifier(field_111210_e, "Weapon modifier", attackDamageWeaponModifier * CorrodibleItemHelper.getModifier(itemStack), 0);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), attributeModifier);
		return multimap;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		CorrodibleItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}

	@Override
	public String manualName(int meta) {
		return getToolMaterialName().toLowerCase() + "Sword";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2};
	}

	@Override
	public int metas() {
		return 0;
	}

	@Override
	public ItemSwordBL setGemTextures(CircleGem gem, String... texture) {
		this.gemTextures[gem.ordinal()] = texture[0];
		return this;
	}

	@Override
	public String[] getGemTextures(CircleGem gem) {
		return new String[]{this.gemTextures[gem.ordinal()]};
	}
}