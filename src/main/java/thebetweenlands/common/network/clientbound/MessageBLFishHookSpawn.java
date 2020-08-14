package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.common.network.MessageBase;

public class MessageBLFishHookSpawn extends MessageBase {
	public int entityPlayerID, entityHookID;

	public MessageBLFishHookSpawn() {
	}

	public MessageBLFishHookSpawn(int playerID, int entityFishHookID) {
		this.entityPlayerID = playerID;
		this.entityHookID = entityFishHookID;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(entityPlayerID);
		buf.writeInt(entityHookID);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.entityPlayerID = buf.readInt();
		this.entityHookID = buf.readInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void handle() {

		World world = FMLClientHandler.instance().getWorldClient();
		Minecraft mc = Minecraft.getMinecraft();

		mc.addScheduledTask(new Runnable() {
			public void run() {
				if (world != null) {
					EntityPlayer playerIn = (EntityPlayer) world.getEntityByID(entityPlayerID);
					EntityBLFishHook entityFishHookIn = (EntityBLFishHook) world.getEntityByID(entityHookID);
					playerIn.fishEntity = entityFishHookIn;
					ObfuscationReflectionHelper.setPrivateValue(EntityBLFishHook.class, entityFishHookIn, playerIn, new String[] { "angler", "field_146042_b" });
					//ObfuscationReflectionHelper.setPrivateValue(EntityFishHook.class, entityFishHookIn, playerIn, new String[] { "angler", "field_146042_b" });
					System.out.println("HELLO?");
					System.out.println("Player HOOK: " + playerIn.fishEntity.getName());
					System.out.println("HOOK Player: " + playerIn.fishEntity.getAngler().getName());
				}
			}
		});
	}
}