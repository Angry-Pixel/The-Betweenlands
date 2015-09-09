package thebetweenlands.mods;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.List;

/**
 * Created by lukas on 09.09.15.
 */
public class RecurrentComplex extends ModRepresentation
{
    public static final String MOD_ID = "reccomplex";

    public static boolean isLoaded()
    {
        return Loader.isModLoaded(MOD_ID);
    }

    public static Item modItem(String id)
    {
        return item(MOD_ID, id);
    }

    public static Block modBlock(String id)
    {
        return block(MOD_ID, id);
    }

    public static void registerDimensionInDictionary(int dimensionID, List<String> types)
    {
        NBTTagCompound message = new NBTTagCompound();

        message.setInteger("dimensionID", dimensionID);

        NBTTagList typesNBT = new NBTTagList();
        for (String type : types)
            typesNBT.appendTag(new NBTTagString(type));
        message.setTag("types", typesNBT);

        FMLInterModComms.sendMessage(MOD_ID, "registerDimension", message);
    }
}
