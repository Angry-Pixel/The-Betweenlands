package thebetweenlands.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

/**
 * Created by bart on 22-7-2015.
 */
public class RecipeBuffers {


    public static String[] bufferCompost;
    public static String[] bufferPAM;
    public static String[] bufferPurify;

    public static void init() {
        compostRecipeBuffer();
        pestleAndMortarRecipeBuffer();
        purifierRecipeBuffer();
    }

    private static void compostRecipeBuffer() {
        for (String aBuffer : bufferCompost) {
            aBuffer = aBuffer.replace("[", "").replace("}", "");
            String[] split = aBuffer.split(":");
            if (split.length >= 4 && split.length <= 5) {
                String arg1, arg2, arg3, arg4, arg5;
                //   modid:input:compost:time[:metadata input], 'oredictionary':oredict name:compost:time or 'id':number id:compost:time[:metadata]

                arg1 = split[0];
                arg2 = split[1];
                arg3 = split[2];
                arg4 = split[3];
                arg5 = (split.length == 5 ? split[4] : "0");

                if (arg1.toLowerCase().equals("oredictionary")) {
                    if (OreDictionary.getOres(arg2).size() > 0 && Integer.parseInt(arg3) > 0 && Integer.parseInt(arg4) > 0)
                        CompostRecipe.addRecipe(Integer.parseInt(arg3), Integer.parseInt(arg4), OreDictionary.getOres(arg2));
                } else if (arg1.toLowerCase().equals("id")) {
                    if (Item.getItemById(Integer.getInteger(arg2)) != null && Integer.parseInt(arg3) > 0 && Integer.parseInt(arg4) > 0 && Integer.parseInt(arg5) >= 0)
                        CompostRecipe.addRecipe(Integer.parseInt(arg3), Integer.parseInt(arg4), Item.getItemById(Integer.getInteger(arg2)), Integer.parseInt(arg5));
                } else if (GameRegistry.findItem(arg1, arg2) != null && Integer.parseInt(arg3) > 0 && Integer.parseInt(arg4) > 0 && Integer.parseInt(arg5) >= 0) {
                    CompostRecipe.addRecipe(Integer.parseInt(arg3), Integer.parseInt(arg4), GameRegistry.findItem(arg1, arg2), Integer.parseInt(arg5));
                } else if (GameRegistry.findBlock(arg1, arg2) != null && Integer.parseInt(arg3) > 0 && Integer.parseInt(arg4) > 0 && Integer.parseInt(arg5) >= 0)
                    CompostRecipe.addRecipe(Integer.parseInt(arg3), Integer.parseInt(arg4), Item.getItemFromBlock(GameRegistry.findBlock(arg1, arg2)), Integer.parseInt(arg5));
            }

        }

    }

    private static void pestleAndMortarRecipeBuffer() {
        for (String aBuffer : bufferPAM) {
            aBuffer = aBuffer.replace("[", "").replace("}", "");
            String[] split = aBuffer.split(":");
            if (split.length >= 4 && split.length <= 12) {
                String arg1, arg2, arg3, arg4;
                //modid:input:modid:output[:'ouputAmount':amount][:'inputAmount':amount][:'metaInput':metadata][:'metaOutput':metadata]
                ArrayList<ItemStack> input = new ArrayList<>();
                ArrayList<ItemStack> output = new ArrayList<>();
                int metaInput = 0;
                int metaOutput = 0;
                int amountInput = 1;
                int amountOutput = 1;

                arg1 = split[0];
                arg2 = split[1];
                arg3 = split[2];
                arg4 = split[3];


                for (int i = 0; i < split.length; i++) {
                    String arg = split[i];
                    if (arg.toLowerCase().equals("metainput")) {
                        metaInput = Integer.parseInt(split[i + 1]);
                        i++;
                    } else if (arg.toLowerCase().equals("metaoutput")) {
                        metaOutput = Integer.parseInt(split[i + 1]);
                        i++;
                    } else if (arg.toLowerCase().equals("inputamount")) {
                        amountInput = Integer.parseInt(split[i + 1]);
                        i++;
                    } else if (arg.toLowerCase().equals("outputamount")) {
                        amountOutput = Integer.parseInt(split[i + 1]);
                        i++;
                    }
                }

                if (arg1.toLowerCase().equals("oredictionary")) {
                    if (OreDictionary.getOres(arg2).size() > 0) {
                        for (ItemStack stack : OreDictionary.getOres(arg2))
                            input.add(new ItemStack(stack.getItem(), amountInput, stack.getItemDamage()));
                    }
                } else if (arg1.toLowerCase().equals("id")) {
                    if (Item.getItemById(Integer.getInteger(arg2)) != null) {
                        input.add(new ItemStack(Item.getItemById(Integer.getInteger(arg2)), amountInput, metaInput));
                    }
                } else if (GameRegistry.findBlock(arg1, arg2) != null) {
                    input.add(new ItemStack(GameRegistry.findBlock(arg1, arg2), amountInput, metaInput));
                } else if (GameRegistry.findItem(arg1, arg2) != null) {
                    input.add(new ItemStack(GameRegistry.findItem(arg1, arg2), amountInput, metaInput));
                }

                if (arg3.toLowerCase().equals("oredictionary")) {
                    if (OreDictionary.getOres(arg4).size() > 0) {
                        for (ItemStack stack : OreDictionary.getOres(arg4))
                            output.add(new ItemStack(stack.getItem(), amountOutput, stack.getItemDamage()));
                    }
                } else if (arg3.toLowerCase().equals("id")) {
                    if (Item.getItemById(Integer.getInteger(arg4)) != null) {
                        output.add(new ItemStack(Item.getItemById(Integer.getInteger(arg4)), amountOutput, metaOutput));
                    }
                } else if (GameRegistry.findBlock(arg3, arg4) != null) {
                    output.add(new ItemStack(GameRegistry.findBlock(arg3, arg4), amountOutput, metaOutput));
                } else if (GameRegistry.findItem(arg3, arg4) != null) {
                    output.add(new ItemStack(GameRegistry.findItem(arg3, arg4), amountOutput, metaOutput));
                }

                for (ItemStack stackInput : input)
                    for (ItemStack stackOutput : output) {
                        PestleAndMortarRecipe.addRecipe(stackOutput, stackInput);
                    }
            }
        }
    }

    private static void purifierRecipeBuffer() {
        for (String aBuffer : bufferPurify) {
            aBuffer = aBuffer.replace("[", "").replace("}", "");
            String[] split = aBuffer.split(":");
            if (split.length >= 4 && split.length <= 12) {
                String arg1, arg2, arg3, arg4;
                //modid:input:modid:output[:'ouputAmount':amount][:'inputAmount':amount][:'metaInput':metadata][:'metaOutput':metadata]
                ArrayList<ItemStack> input = new ArrayList<>();
                ArrayList<ItemStack> output = new ArrayList<>();
                int metaInput = 0;
                int metaOutput = 0;
                int amountInput = 1;
                int amountOutput = 1;

                arg1 = split[0];
                arg2 = split[1];
                arg3 = split[2];
                arg4 = split[3];


                for (int i = 0; i < split.length; i++) {
                    String arg = split[i];
                    if (arg.toLowerCase().equals("metainput")) {
                        metaInput = Integer.parseInt(split[i + 1]);
                        i++;
                    } else if (arg.toLowerCase().equals("metaoutput")) {
                        metaOutput = Integer.parseInt(split[i + 1]);
                        i++;
                    } else if (arg.toLowerCase().equals("inputamount")) {
                        amountInput = Integer.parseInt(split[i + 1]);
                        i++;
                    } else if (arg.toLowerCase().equals("outputamount")) {
                        amountOutput = Integer.parseInt(split[i + 1]);
                        i++;
                    }
                }

                if (arg1.toLowerCase().equals("oredictionary")) {
                    if (OreDictionary.getOres(arg2).size() > 0) {
                        for (ItemStack stack : OreDictionary.getOres(arg2))
                            input.add(new ItemStack(stack.getItem(), amountInput, stack.getItemDamage()));
                    }
                } else if (arg1.toLowerCase().equals("id")) {
                    if (Item.getItemById(Integer.getInteger(arg2)) != null) {
                        input.add(new ItemStack(Item.getItemById(Integer.getInteger(arg2)), amountInput, metaInput));
                    }
                } else if (GameRegistry.findBlock(arg1, arg2) != null) {
                    input.add(new ItemStack(GameRegistry.findBlock(arg1, arg2), amountInput, metaInput));
                } else if (GameRegistry.findItem(arg1, arg2) != null) {
                    input.add(new ItemStack(GameRegistry.findItem(arg1, arg2), amountInput, metaInput));
                }

                if (arg3.toLowerCase().equals("oredictionary")) {
                    if (OreDictionary.getOres(arg4).size() > 0) {
                        for (ItemStack stack : OreDictionary.getOres(arg4))
                            output.add(new ItemStack(stack.getItem(), amountOutput, stack.getItemDamage()));
                    }
                } else if (arg3.toLowerCase().equals("id")) {
                    if (Item.getItemById(Integer.getInteger(arg4)) != null) {
                        output.add(new ItemStack(Item.getItemById(Integer.getInteger(arg4)), amountOutput, metaOutput));
                    }
                } else if (GameRegistry.findBlock(arg3, arg4) != null) {
                    output.add(new ItemStack(GameRegistry.findBlock(arg3, arg4), amountOutput, metaOutput));
                } else if (GameRegistry.findItem(arg3, arg4) != null) {
                    output.add(new ItemStack(GameRegistry.findItem(arg3, arg4), amountOutput, metaOutput));
                }

                for (ItemStack stackInput : input)
                    for (ItemStack stackOutput : output)
                        PurifierRecipe.addRecipe(stackOutput, stackInput);
            }
        }
    }
}
