package thebetweenlands.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tooltips.HeldItemTooltipHandler;
import thebetweenlands.items.ICorrodible;

public class BLPlayerControllerMP extends PlayerControllerMP {
	//TODO: Maybe find a better way to set PlayerControllerMP#currentItemHittingBlock after items have been synced 

	private final PlayerControllerMP parent;

	private static final Field field_currentItemHittingBlock = ReflectionHelper.findField(PlayerControllerMP.class, "currentItemHittingBlock", "field_85183_f", "f");
	private static final List<String> exclusions = new ArrayList<String>();

	static {
		exclusions.add("Corrosion");
	}

	public BLPlayerControllerMP(Minecraft mc, NetHandlerPlayClient netHandler, PlayerControllerMP parent) {
		super(mc, netHandler);
		this.parent = parent;
	}

	@Override
	public void onPlayerDamageBlock(int p_78759_1_, int p_78759_2_, int p_78759_3_, int p_78759_4_) {
		try {
			ItemStack prev = (ItemStack) field_currentItemHittingBlock.get(this);
			ItemStack current = Minecraft.getMinecraft().thePlayer.getHeldItem();
			if(current != null && prev != null && !prev.equals(current) && prev.getItem() instanceof ICorrodible) {
				if(HeldItemTooltipHandler.areItemStackTagsEqual(prev, current, exclusions)) {
					field_currentItemHittingBlock.set(this, current);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		super.onPlayerDamageBlock(p_78759_1_, p_78759_2_, p_78759_3_, p_78759_4_);
	}
}
