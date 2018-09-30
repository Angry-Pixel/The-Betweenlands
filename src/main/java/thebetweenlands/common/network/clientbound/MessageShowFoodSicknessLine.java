package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.network.MessageBase;

public class MessageShowFoodSicknessLine extends MessageBase {
	private FoodSickness sickness;
	private ItemStack stack;

	public MessageShowFoodSicknessLine() {}

	public MessageShowFoodSicknessLine(ItemStack stack, FoodSickness sickness) {
		this.sickness = sickness;
		this.stack = stack.copy();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeEnumValue(this.sickness);
		buf.writeItemStack(this.stack);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.sickness = buf.readEnumValue(FoodSickness.class);
		try {
			this.stack = buf.readItemStack();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.showMessage(this.stack, this.sickness);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void showMessage(ItemStack stack, FoodSickness sickness) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null) {
			player.sendStatusMessage(new TextComponentString(String.format(sickness.getRandomLine(player.getRNG()), stack.getDisplayName())), true);
		}
	}
}
