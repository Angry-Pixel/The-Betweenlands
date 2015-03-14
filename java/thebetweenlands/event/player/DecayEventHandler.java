package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import org.lwjgl.input.Keyboard;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.network.MessageSyncPlayerDecay;
import thebetweenlands.utils.IDecayFood;

public class DecayEventHandler
{
    public static DecayEventHandler INSTANCE = new DecayEventHandler();

    public PotionEffect slowness_0 = new PotionEffect(Potion.moveSlowdown.getId(), 1, 0, true);
    public PotionEffect slowness_1 = new PotionEffect(Potion.moveSlowdown.getId(), 1, 1, true);
    public PotionEffect slowness_2 = new PotionEffect(Potion.moveSlowdown.getId(), 1, 2, true);

    @SubscribeEvent
    public void entityConstructing(EntityEvent.EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            event.entity.registerExtendedProperties(EntityPropertiesDecay.getId(), new EntityPropertiesDecay());
        }
    }

    @SubscribeEvent
    public void joinWorld(EntityJoinWorldEvent event)
    {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
        {
            int decayLevel = ((EntityPropertiesDecay) event.entity.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel;
            TheBetweenlands.networkWrapper.sendTo(new MessageSyncPlayerDecay(decayLevel), (EntityPlayerMP) event.entity);
        }
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event)
    {
        if (DecayManager.enableDecay(Minecraft.getMinecraft().thePlayer) && Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()))
        {
            DecayManager.resetDecay(Minecraft.getMinecraft().thePlayer);
        }
    }

    @SubscribeEvent
    public void useItem(PlayerUseItemEvent.Finish event)
    {
        if (DecayManager.enableDecay(event.entityPlayer) && event.item.getItem() instanceof IDecayFood)
        {
            IDecayFood food = (IDecayFood) event.item.getItem();
            DecayManager.setDecayLevel(DecayManager.getDecayLevel(event.entityPlayer) + food.getDecayHealAmount(), event.entityPlayer);
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (DecayManager.enableDecay(event.player))
        {
            event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(DecayManager.getPlayerHearts(event.player));

            if (DecayManager.getDecayLevel(event.player) <= 4) event.player.addPotionEffect(slowness_2);
            else if (DecayManager.getDecayLevel(event.player) <= 8) event.player.addPotionEffect(slowness_1);
            else if (DecayManager.getDecayLevel(event.player) <= 12) event.player.addPotionEffect(slowness_0);
        }
        else if (event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue() != 20d)
        {
            event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20d);
        }
    }
}
