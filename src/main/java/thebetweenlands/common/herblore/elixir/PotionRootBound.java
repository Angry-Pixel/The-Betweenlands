package thebetweenlands.common.herblore.elixir;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;

public class PotionRootBound extends Potion {
	public PotionRootBound() {
		super(true, 5926017);
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "root_bound"));
		this.setPotionName("bl.potion.rootBound");
		this.setIconIndex(1, 0);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "be8859c1-024b-4f31-b606-5991011ddd98", -1, 2);
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
