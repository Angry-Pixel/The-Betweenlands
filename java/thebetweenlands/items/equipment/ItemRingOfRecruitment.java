package thebetweenlands.items.equipment;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.items.loot.ItemRing;
import thebetweenlands.manual.IManualEntryItem;

public class ItemRingOfRecruitment extends ItemRing implements IManualEntryItem {
	public ItemRingOfRecruitment() {
		super();
		this.setMaxDamage(450);
		this.setUnlocalizedName("thebetweenlands.ringOfRecruitment");
		this.setTextureName("thebetweenlands:ringOfRecruitment");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("ring.recruitment.bonus"));
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && StatCollector.translateToLocal("item.thebetweenlands.ringOfRecruitment.tooltip").length() > 10) {
            String toolTip = StatCollector.translateToLocal("item.thebetweenlands.ringOfRecruitment.tooltip");
            String[] toolTopWords = toolTip.split(" ");
            String temp = "";
            for (String string : toolTopWords){
                if ((temp + string).length() >= 25) {
                    list.add(temp);
                    temp = "";
                } else {
                    temp += " " + string;
                }
            }
            if (temp.length() > 0)
                list.add(temp);
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
