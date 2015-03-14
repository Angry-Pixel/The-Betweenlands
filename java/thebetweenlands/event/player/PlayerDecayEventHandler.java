package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.network.MessageSyncPlayerDecay;

public class PlayerDecayEventHandler
{
    public static PlayerDecayEventHandler INSTANCE = new PlayerDecayEventHandler();

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
    public void livingJump(LivingEvent.LivingJumpEvent event)
    {
        if (event.entity instanceof EntityPlayer && event.entity.worldObj.isRemote)
        {
            EntityPlayer player = (EntityPlayer) event.entity;
            DecayManager.setDecayLevel(DecayManager.getDecayLevel(player) + 1, player);
        }
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event)
    {
        if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode()))
        {
            DecayManager.resetDecay(Minecraft.getMinecraft().thePlayer);
        }
    }
}
