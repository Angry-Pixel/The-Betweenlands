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
                System.out.println(name);
                if (name.contains("recipe")) {
                    jsonReader.beginObject();
                    String inputType = "";
                    String inputItem = "";
                    int compostTime = 0;
                    int compostAmount = 0;
                    String outputType = "";
                    String outputItem = "";
                    int metaInput = 0;
                    int metaOutput = 0;
                    int inputAmount = 0;
                    int outputAmount = 0;
                    while (jsonReader.hasNext()) {
                        if (recipeName.equals("compost")) {
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
                        } else if (recipeName.equals("pam") || recipeName.equals("pestleAndMortar") || recipeName.equals("purifier")) {
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
                                    metaInput = jsonReader.nextInt();
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
}
