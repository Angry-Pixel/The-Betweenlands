package thebetweenlands.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.utils.IGemTextureProvider;

public class ItemArmorBL extends ItemArmor implements IGemTextureProvider {
	public final String armorTexture1, armorTexture2;
	private IIcon gemItemIcons[] = new IIcon[CircleGem.TYPES.length];
	private String gemTextures[][] = new String[CircleGem.TYPES.length][0];

	public ItemArmorBL(ArmorMaterial material, int renderIndex, int armorType, String armorTexture1, String armorTexture2) {
		super(material, renderIndex, armorType);
		this.armorTexture1 = armorTexture1;
		this.armorTexture2 = armorTexture2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		for(int i = 0; i < this.gemTextures.length; i++) {
			if(this.gemTextures[i].length > 2 && this.gemTextures[i][0] != null) {
				this.gemItemIcons[i] = register.registerIcon(this.gemTextures[i][0]);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack) {
		CircleGem gem = CircleGem.getGem(stack);
		if(gem != CircleGem.NONE && this.gemTextures[gem.ordinal()].length > 2 && this.gemTextures[gem.ordinal()][0] != null) {
			return this.gemItemIcons[gem.ordinal()];
		}
		return super.getIconIndex(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity event, int slot, String type) {
		CircleGem gem = CircleGem.getGem(stack);
		if (this.isLeggings(stack)) {
			if(gem != CircleGem.NONE) {
				if(this.gemTextures[gem.ordinal()].length >= 3) {
					return this.gemTextures[gem.ordinal()][2];
				} else {
					return this.gemTextures[gem.ordinal()][1];
				}
			}
			if(this.armorTexture2 != null) {
				return this.armorTexture2;
			}
			return this.armorTexture1;
		} else {
			if(gem != CircleGem.NONE) {
				if(this.gemTextures[gem.ordinal()].length >= 3) {
					return this.gemTextures[gem.ordinal()][1];
				} else {
					return this.gemTextures[gem.ordinal()][0];
				}
			}
			return this.armorTexture1;
		}
	}

	protected boolean isLeggings(ItemStack stack) {
		return false;
	}

	/**
	 * Registers alternative gem textures.
	 * Args: Gem, Item texture (optional), Armor texture 1, Armor texture 2
	 */
	@Override
	public ItemArmorBL setGemTextures(CircleGem gem, String... textures) {
		this.gemTextures[gem.ordinal()] = textures;
		return this;
	}

	@Override
	public String[] getGemTextures(CircleGem gem) {
		return this.gemTextures[gem.ordinal()];
	}
	
	@Override
	 public int getItemEnchantability() {
	  return 0;
	 }
	 @Override
	 public boolean isBookEnchantable(ItemStack is, ItemStack book) {
	  return false;
	 }
}
