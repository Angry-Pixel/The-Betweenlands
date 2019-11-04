package thebetweenlands.common.herblore.elixir;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class PotionEnlightened extends Potion {
	public PotionEnlightened() {
		super(false, 5926017);
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "enlightened"));
		this.setPotionName("bl.potion.enlightened");
		this.setIconIndex(1, 0);
	}

	@Override
	public boolean shouldRender(PotionEffect effect) {
		return false;
	}

	@Override
	public boolean shouldRenderHUD(PotionEffect effect) {
		return false;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.emptyList();
	}
}
