package thebetweenlands.network;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.network.base.AbstractMessage;
import thebetweenlands.tileentities.TileEntityDruidAltar;

import java.util.HashMap;

public class MessageAltarCraftingProgress extends AbstractMessage<MessageAltarCraftingProgress>
{
    private static final ResourceLocation soundLocation = new ResourceLocation("thebetweenlands:druidChant");
    private static final HashMap<String, PositionedSoundDC> tileSoundMap = Maps.newHashMap();

    public int posX;
    public int posY;
    public int posZ;
    public int craftingProgress;

    @SideOnly(Side.CLIENT)
    class PositionedSoundDC extends PositionedSound
    {
        protected PositionedSoundDC(ResourceLocation resourceLocation)
        {
            super(resourceLocation);
        }

        public PositionedSoundDC setPos(float x, float y, float z)
        {
            xPosF = x;
            yPosF = y;
            zPosF = z;
            return this;
        }
    }

    public MessageAltarCraftingProgress()
    {

    }

    public MessageAltarCraftingProgress(int x, int y, int z, int progress)
    {
        posX = x;
        posY = y;
        posZ = z;
        craftingProgress = progress;
    }

    public void onClientMessage(MessageAltarCraftingProgress message, EntityPlayer player)
    {
        World world = FMLClientHandler.instance().getWorldClient();
        if (world != null && world.isRemote)
        {
            TileEntity te = world.getTileEntity(message.posX, message.posY, message.posZ);
            if (te instanceof TileEntityDruidAltar)
            {
                TileEntityDruidAltar teda = (TileEntityDruidAltar) te;
                if (message.craftingProgress < 0)
                {
                    SoundHandler mcSoundHandler = Minecraft.getMinecraft().getSoundHandler();
                    if (message.craftingProgress == -1)
                    {
                        PositionedSoundDC sound = tileSoundMap.get(teda.xCoord + "|" + teda.yCoord + "|" + teda.zCoord);
                        if (sound == null)
                        {
                            sound = new PositionedSoundDC(soundLocation).setPos(message.posX, message.posY, message.posZ);
                            tileSoundMap.put(message.posX + "|" + message.posY + "|" + message.posZ, sound);
                        }
                        else
                        {
                            if (mcSoundHandler.isSoundPlaying(sound))
                            {
                                mcSoundHandler.stopSound(sound);
                            }
                        }
                        mcSoundHandler.playSound(sound);

                        for (int x = -8; x < 8; x++)
                        {
                            for (int y = -8; y < 8; y++)
                            {
                                for (int z = -8; z < 8; z++)
                                {
                                    Block block = world.getBlock(teda.xCoord + x, teda.yCoord + y, teda.zCoord + z);
                                    if (block == BLBlockRegistry.druidStone1 || block == BLBlockRegistry.druidStone2 || block == BLBlockRegistry.druidStone3 || block == BLBlockRegistry.druidStone4 || block == BLBlockRegistry.druidStone5)
                                    {
                                        TheBetweenlands.proxy.spawnCustomParticle("altarcrafting", world, te.xCoord + x, te.yCoord + y, te.zCoord + z, 0, 0, 0, 1.0F, teda);
                                    }
                                }
                            }
                        }
                    }
                    else if (message.craftingProgress == -2)
                    {
                        PositionedSoundDC sound = tileSoundMap.get(message.posX + "|" + message.posY + "|" + message.posZ);
                        if (sound != null)
                        {
                            if (mcSoundHandler.isSoundPlaying(sound))
                            {
                                mcSoundHandler.stopSound(sound);
                            }
                        }
                    }
                }
                else
                {
                    ((TileEntityDruidAltar) te).craftingProgress = message.craftingProgress;
                }
            }
        }
    }

    public void onServerMessage(MessageAltarCraftingProgress message, EntityPlayer player)
    {

    }

    public void fromBytes(ByteBuf buf)
    {
        posX = buf.readInt();
        posY = buf.readInt();
        posZ = buf.readInt();
        craftingProgress = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeInt(posZ);
        buf.writeInt(craftingProgress);
    }
}
