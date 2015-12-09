package thebetweenlands.recipes;

import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bart on 22-7-2015.
 */
public class RecipeBuffers {
    public static void readJson(JsonReader jsonReader) throws IOException {
        while (jsonReader.hasNext()) {
            String recipeName = jsonReader.nextName();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.contains("recipe")) {
                    jsonReader.beginObject();
                    String inputType = "";
                    String inputItem = "";
                    String inputType2 = "";
                    String inputItem2 = "";
                    String inputType3 = "";
                    String inputItem3 = "";
                    String inputType4 = "";
                    String inputItem4 = "";
                    int compostTime = 0;
                    int compostAmount = 0;
                    String outputType = "";
                    String outputItem = "";
                    int metaInput = 0;
                    int metaOutput = 0;
                    int metaInput2 = 0;
                    int metaInput3 = 0;
                    int metaInput4 = 0;
                    int inputAmount = 1;
                    int outputAmount = 1;
                    int fuelAmount = 1;
                    int lifeAmount = 1;
                    while (jsonReader.hasNext()) {
                        switch (recipeName) {
                            case "compost": {
                                String n = jsonReader.nextName();
                                switch (n) {
                                    case "inputType":
                                        inputType = jsonReader.nextString();
                                        break;
                                    case "inputItem":
                                        inputItem = jsonReader.nextString();
                                        break;
                                    case "compostTime":
                                        compostTime = jsonReader.nextInt();
                                        break;
                                    case "compostAmount":
                                        compostAmount = jsonReader.nextInt();
                                        break;
                                    case "meta":
                                    case "metaInput":
                                        metaInput = jsonReader.nextInt();
                                        break;
                                }
                                break;
                            }
                            case "animator":{
                                String n = jsonReader.nextName();
                                switch (n) {
                                    case "inputType":
                                        inputType = jsonReader.nextString();
                                        break;
                                    case "inputItem":
                                        inputItem = jsonReader.nextString();
                                        break;
                                    case "outputType":
                                        outputType = jsonReader.nextString();
                                        break;
                                    case "outputItem":
                                        outputItem = jsonReader.nextString();
                                        break;
                                    case "metaInput":
                                        metaInput = jsonReader.nextInt();
                                        break;
                                    case "metaOutput":
                                        metaOutput = jsonReader.nextInt();
                                        break;
                                    case "outputAmount":
                                        outputAmount = jsonReader.nextInt();
                                        break;
                                    case "lifeAmount":
                                        lifeAmount = jsonReader.nextInt();
                                        break;
                                    case "fuelAmount":
                                        fuelAmount = jsonReader.nextInt();
                                        break;
                                }
                                break;
                            }
                            case "pam":
                            case "pestleAndMortar":
                            case "purifier": {
                                String n = jsonReader.nextName();
                                switch (n) {
                                    case "inputType":
                                        inputType = jsonReader.nextString();
                                        break;
                                    case "inputItem":
                                        inputItem = jsonReader.nextString();
                                        break;
                                    case "outputType":
                                        outputType = jsonReader.nextString();
                                        break;
                                    case "outputItem":
                                        outputItem = jsonReader.nextString();
                                        break;
                                    case "inputAmount":
                                        inputAmount = jsonReader.nextInt();
                                        break;
                                    case "outputAmount":
                                        outputAmount = jsonReader.nextInt();
                                        break;
                                    case "metaInput":
                                        metaInput = jsonReader.nextInt();
                                        break;
                                    case "metaOutput":
                                        metaOutput = jsonReader.nextInt();
                                        break;
                                }
                                break;
                            }
                            case "druidAltar": {
                                String n = jsonReader.nextName();
                                switch (n) {
                                    case "inputType":
                                        inputType = jsonReader.nextString();
                                        break;
                                    case "inputItem":
                                        inputItem = jsonReader.nextString();
                                        break;
                                    case "metaInput":
                                        metaInput = jsonReader.nextInt();
                                        break;
                                    case "inputType2":
                                        inputType2 = jsonReader.nextString();
                                        break;
                                    case "inputItem2":
                                        inputItem2 = jsonReader.nextString();
                                        break;
                                    case "metaInput2":
                                        metaInput2 = jsonReader.nextInt();
                                        break;
                                    case "inputType3":
                                        inputType3 = jsonReader.nextString();
                                        break;
                                    case "inputItem3":
                                        inputItem3 = jsonReader.nextString();
                                        break;
                                    case "metaInput3":
                                        metaInput3 = jsonReader.nextInt();
                                        break;
                                    case "inputType4":
                                        inputType4 = jsonReader.nextString();
                                        break;
                                    case "inputItem4":
                                        inputItem4 = jsonReader.nextString();
                                        break;
                                    case "metaInput4":
                                        metaInput4 = jsonReader.nextInt();
                                        break;
                                    case "metaOutput":
                                        metaOutput = jsonReader.nextInt();
                                        break;
                                    case "outputType":
                                        outputType = jsonReader.nextString();
                                        break;
                                    case "outputItem":
                                        outputItem = jsonReader.nextString();
                                        break;
                                }
                                break;
                            }
                        }
                    }
                    switch (recipeName) {
                        case "compost":
                            compostRecipeBuffer(inputType, inputItem, compostTime, compostAmount, metaInput);
                            break;
                        case "pam":
                        case "pestleAndMortar":
                            pestleAndMortarRecipeBuffer(inputType, inputItem, outputType, outputItem, inputAmount, outputAmount, metaInput, metaOutput);
                            break;
                        case "purifier":
                            purifierRecipeBuffer(inputType, inputItem, outputType, outputItem, inputAmount, outputAmount, metaInput, metaOutput);
                            break;
                        case "druidAltar":
                            druidAltarRecipeBuffer(inputType, inputItem, inputType2, inputItem2, inputType3, inputItem3, inputType4, inputItem4, outputType, outputItem, metaInput, metaInput2, metaInput3, metaInput4, metaOutput);
                            break;
                        case "animator":
                            animatorRecipeBuffer(inputType, inputItem, outputType, outputItem, outputAmount, fuelAmount, lifeAmount, metaInput, metaOutput);
                            break;
                    }
                    jsonReader.endObject();
                }
            }
            jsonReader.endObject();
        }
    }


    private static void compostRecipeBuffer(String inputType, String inputItem, int compostTime, int compostAmount, int meta) {
        if (inputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem).size() > 0 && compostTime > 0 && compostAmount > 0)
                CompostRecipe.addRecipe(compostTime, compostAmount, OreDictionary.getOres(inputItem));
        } else if (inputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem)) != null && compostTime > 0 && compostAmount > 0 && meta >= 0)
                CompostRecipe.addRecipe(compostTime, compostAmount, Item.getItemById(Integer.getInteger(inputItem)), meta);
        } else if (GameRegistry.findItem(inputType, inputItem) != null && compostTime > 0 && compostAmount > 0 && meta >= 0) {
            CompostRecipe.addRecipe(compostTime, compostAmount, GameRegistry.findItem(inputType, inputItem), meta);
        } else if (GameRegistry.findBlock(inputType, inputItem) != null && compostTime > 0 && compostAmount > 0 && meta >= 0)
            CompostRecipe.addRecipe(compostAmount, compostAmount, Item.getItemFromBlock(GameRegistry.findBlock(inputType, inputItem)), meta);
    }

    private static void pestleAndMortarRecipeBuffer(String inputType, String inputItem, String outputType, String outputItem, int inputAmount, int outputAmount, int metaInput, int metaOutput) {
        ArrayList<ItemStack> input = new ArrayList<>();
        ArrayList<ItemStack> output = new ArrayList<>();

        if (inputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(inputItem))
                    input.add(new ItemStack(stack.getItem(), inputAmount, stack.getItemDamage()));
            }
        } else if (inputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem)) != null) {
                input.add(new ItemStack(Item.getItemById(Integer.getInteger(inputItem)), inputAmount, metaInput));
            }
        } else if (GameRegistry.findBlock(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findBlock(inputType, inputItem), inputAmount, metaInput));
        } else if (GameRegistry.findItem(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findItem(inputType, inputItem), inputAmount, metaInput));
        }

        if (outputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(outputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(outputItem))
                    output.add(new ItemStack(stack.getItem(), outputAmount, stack.getItemDamage()));
            }
        } else if (outputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(outputItem)) != null) {
                output.add(new ItemStack(Item.getItemById(Integer.getInteger(outputItem)), outputAmount, metaOutput));
            }
        } else if (GameRegistry.findBlock(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findBlock(outputType, outputItem), outputAmount, metaOutput));
        } else if (GameRegistry.findItem(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findItem(outputType, outputItem), outputAmount, metaOutput));
        }

        for (ItemStack stackInput : input)
            for (ItemStack stackOutput : output) {
                PestleAndMortarRecipe.addRecipe(stackOutput, stackInput);
            }
    }

    private static void purifierRecipeBuffer(String inputType, String inputItem, String outputType, String outputItem, int inputAmount, int outputAmount, int metaInput, int metaOutput) {
        ArrayList<ItemStack> input = new ArrayList<>();
        ArrayList<ItemStack> output = new ArrayList<>();

        if (inputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(inputItem))
                    input.add(new ItemStack(stack.getItem(), inputAmount, stack.getItemDamage()));
            }
        } else if (inputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem)) != null) {
                input.add(new ItemStack(Item.getItemById(Integer.getInteger(inputItem)), inputAmount, metaInput));
            }
        } else if (GameRegistry.findBlock(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findBlock(inputType, inputItem), inputAmount, metaInput));
        } else if (GameRegistry.findItem(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findItem(inputType, inputItem), inputAmount, metaInput));
        }

        if (outputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(outputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(outputItem))
                    output.add(new ItemStack(stack.getItem(), outputAmount, stack.getItemDamage()));
            }
        } else if (outputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(outputItem)) != null) {
                output.add(new ItemStack(Item.getItemById(Integer.getInteger(outputItem)), outputAmount, metaOutput));
            }
        } else if (GameRegistry.findBlock(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findBlock(outputType, outputItem), outputAmount, metaOutput));
        } else if (GameRegistry.findItem(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findItem(outputType, outputItem), outputAmount, metaOutput));
        }

        for (ItemStack stackInput : input)
            for (ItemStack stackOutput : output)
                PurifierRecipe.addRecipe(stackOutput, stackInput);
    }

    private static void druidAltarRecipeBuffer(String inputType, String inputItem, String inputType2, String inputItem2, String inputType3, String inputItem3, String inputType4, String inputItem4, String outputType, String outputItem, int metaInput, int metaInput2, int metaInput3, int metaInput4, int metaOutput) {
        ArrayList<ItemStack> input = new ArrayList<>();
        ArrayList<ItemStack> input2 = new ArrayList<>();
        ArrayList<ItemStack> input3 = new ArrayList<>();
        ArrayList<ItemStack> input4 = new ArrayList<>();
        ArrayList<ItemStack> output = new ArrayList<>();

        if (inputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(inputItem))
                    input.add(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
            }
        } else if (inputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem)) != null) {
                input.add(new ItemStack(Item.getItemById(Integer.getInteger(inputItem)), 1, metaInput));
            }
        } else if (GameRegistry.findBlock(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findBlock(inputType, inputItem), 1, metaInput));
        } else if (GameRegistry.findItem(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findItem(inputType, inputItem), 1, metaInput));
        }

        if (inputType2.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem2).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(inputItem2))
                    input2.add(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
            }
        } else if (inputType2.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem2)) != null) {
                input2.add(new ItemStack(Item.getItemById(Integer.getInteger(inputItem2)), 1, metaInput2));
            }
        } else if (GameRegistry.findBlock(inputType2, inputItem2) != null) {
            input2.add(new ItemStack(GameRegistry.findBlock(inputType2, inputItem2), 1, metaInput2));
        } else if (GameRegistry.findItem(inputType2, inputItem2) != null) {
            input2.add(new ItemStack(GameRegistry.findItem(inputType2, inputItem2), 1, metaInput2));
        }

        if (inputType3.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem3).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(inputItem3))
                    input3.add(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
            }
        } else if (inputType3.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem3)) != null) {
                input3.add(new ItemStack(Item.getItemById(Integer.getInteger(inputItem3)), 1, metaInput3));
            }
        } else if (GameRegistry.findBlock(inputType3, inputItem3) != null) {
            input3.add(new ItemStack(GameRegistry.findBlock(inputType3, inputItem3), 1, metaInput3));
        } else if (GameRegistry.findItem(inputType3, inputItem3) != null) {
            input3.add(new ItemStack(GameRegistry.findItem(inputType3, inputItem3), 1, metaInput3));
        }

        if (inputType4.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem4).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(inputItem4))
                    input4.add(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
            }
        } else if (inputType4.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem4)) != null) {
                input4.add(new ItemStack(Item.getItemById(Integer.getInteger(inputItem4)), 1, metaInput4));
            }
        } else if (GameRegistry.findBlock(inputType4, inputItem4) != null) {
            input4.add(new ItemStack(GameRegistry.findBlock(inputType4, inputItem4), 1, metaInput4));
        } else if (GameRegistry.findItem(inputType4, inputItem4) != null) {
            input4.add(new ItemStack(GameRegistry.findItem(inputType4, inputItem4), 1, metaInput4));
        }

        if (outputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(outputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(outputItem))
                    output.add(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
            }
        } else if (outputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(outputItem)) != null) {
                output.add(new ItemStack(Item.getItemById(Integer.getInteger(outputItem)), 1, metaOutput));
            }
        } else if (GameRegistry.findBlock(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findBlock(outputType, outputItem), 1, metaOutput));
        } else if (GameRegistry.findItem(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findItem(outputType, outputItem), 1, metaOutput));
        }

        for (ItemStack stackInput : input)
            for (ItemStack stackInput2 : input2)
                for (ItemStack stackInput3 : input3)
                    for (ItemStack stackInput4 : input4)
                        for (ItemStack stackOutput : output)
                            DruidAltarRecipe.addRecipe(stackInput, stackInput2, stackInput3, stackInput4, stackOutput);
    }


    private static void animatorRecipeBuffer(String inputType, String inputItem, String outputType, String outputItem, int outputAmount, int fuelAmount, int lifeAmount, int metaInput, int metaOutput){
        ArrayList<ItemStack> input = new ArrayList<>();
        ArrayList<ItemStack> output = new ArrayList<>();

        if (inputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(inputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(inputItem))
                    input.add(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
            }
        } else if (inputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(inputItem)) != null) {
                input.add(new ItemStack(Item.getItemById(Integer.getInteger(inputItem)), 1, metaInput));
            }
        } else if (GameRegistry.findBlock(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findBlock(inputType, inputItem), 1, metaInput));
        } else if (GameRegistry.findItem(inputType, inputItem) != null) {
            input.add(new ItemStack(GameRegistry.findItem(inputType, inputItem), 1, metaInput));
        }

        if (outputType.toLowerCase().equals("oredictionary")) {
            if (OreDictionary.getOres(outputItem).size() > 0) {
                for (ItemStack stack : OreDictionary.getOres(outputItem))
                    output.add(new ItemStack(stack.getItem(), outputAmount, stack.getItemDamage()));
            }
        } else if (outputType.toLowerCase().equals("id")) {
            if (Item.getItemById(Integer.getInteger(outputItem)) != null) {
                output.add(new ItemStack(Item.getItemById(Integer.getInteger(outputItem)), outputAmount, metaOutput));
            }
        } else if (GameRegistry.findBlock(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findBlock(outputType, outputItem), outputAmount, metaOutput));
        } else if (GameRegistry.findItem(outputType, outputItem) != null) {
            output.add(new ItemStack(GameRegistry.findItem(outputType, outputItem), outputAmount, metaOutput));
        }

        for (ItemStack stackInput : input)
            for (ItemStack stackOutput : output)
                AnimatorRecipe.addRecipe(new AnimatorRecipe(stackInput, fuelAmount, lifeAmount, stackOutput));
    }
}
