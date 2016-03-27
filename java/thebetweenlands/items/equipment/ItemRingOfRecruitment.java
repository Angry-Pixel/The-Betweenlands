package thebetweenlands.items.equipment;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.event.item.ItemTooltipHandler;
import thebetweenlands.items.loot.ItemRing;
import thebetweenlands.manual.IManualEntryItem;

public class ItemRingOfRecruitment extends ItemRing implements IManualEntryItem {
	public ItemRingOfRecruitment() {
		super();
		this.setMaxDamage(450);
		this.setUnlocalizedName("thebetweenlands.ringOfRecruitment");
		this.setTextureName("thebetweenlands:ringOfRecruitment");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("ring.recruitment.bonus"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && StatCollector.translateToLocal("item.thebetweenlands.ringOfRecruitment.tooltip").length() > 10) {
			String toolTip = StatCollector.translateToLocal("item.thebetweenlands.ringOfRecruitment.tooltip");
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(StatCollector.translateToLocal("item.thebetweenlands.press.shift"));
		}
	}

	@Override
	public String manualName(int meta) {
		return "ringOfRecruitment";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{6};
	}

	@Override
	public int metas() {
		return 0;
	}
}
