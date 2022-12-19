package thebetweenlands.util;

public class RecipeHelper {
//    public static void addRecipe(ItemStack output, Object... recipeComponents) {
//        StringBuilder fileContent = new StringBuilder("{ \n");
//        fileContent.append("\"type\": \"minecraft:crafting_shaped\", \n");
//        fileContent.append("\"pattern\": [\n");
//        boolean strings = true;
//        boolean firstChar = true;
//        boolean firstString = true;
//        for (int i = 0; i < recipeComponents.length; i++) {
//            Object o = recipeComponents[i];
//            if (o instanceof Character) {
//                if (!firstChar)
//                    fileContent.append(", \n");
//                else
//                    fileContent.append("\n");
//                if (strings) {
//                    strings = false;
//                    fileContent.append("\n],\n");
//                    fileContent.append("  \"key\": {\n");
//                }
//                fileContent.append(String.format("\"%s\": {\n", o));
//                Object itemStack = recipeComponents[++i];
//                if (itemStack instanceof Block)
//                    itemStack = new ItemStack((Block) itemStack);
//
//
//                if (itemStack instanceof ItemStack) {
//
//                    fileContent.append(String.format("\"item\": \"%s\"", ((ItemStack) itemStack).getItem().getRegistryName()));
//                    if (((ItemStack) itemStack).getItemDamage() > 0)
//                        fileContent.append(String.format(", \n\"data\": %s", ((ItemStack) itemStack).getItemDamage()));
//                    else
//                        fileContent.append("\n");
//
//                    if (((ItemStack) itemStack).getCount() > 1)
//                        fileContent.append(String.format(", \n\"count\": %s", ((ItemStack) itemStack).getCount()));
//                    fileContent.append("}");
//
//                    firstChar = false;
//                }
//            } else if (o instanceof String) {
//                if (!firstString)
//                    fileContent.append(", \n");
//                fileContent.append(String.format("\"%s\"", o));
//                firstString = false;
//            }
//        }
//
//        fileContent.append("},\"result\": {\n");
//
//        fileContent.append(String.format("\"item\": \"%s\"", output.getItem().getRegistryName()));
//        if (output.getItemDamage() > 0)
//            fileContent.append(String.format(", \n\"data\": %s", output.getItemDamage()));
//        else
//            fileContent.append("\n");
//        if (output.getCount() > 1)
//            fileContent.append(String.format(", \n\"count\": %s", output.getCount()));
//        fileContent.append("}\n}");
//        PrintWriter writer = null;
//        try {
//            File f = new File("C:/Users/Bart/Desktop/json/" + output.getItem().getRegistryName().toString().replace("thebetweenlands:", "") + ".json");
//            writer = new PrintWriter(f, "UTF-8");
//        } catch (FileNotFoundException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        writer.println(fileContent);
//        writer.close();
//    }
//
//	public static IRecipe addShapedRecipe(IForgeRegistry<IRecipe> registry, ItemStack output, Object... recipeComponents) {
//		ShapedRecipes recipe = CraftingManager.getInstance().addRecipe(output, recipeComponents);
//		CraftingManager.getInstance().getRecipeList().remove(recipe);
//		ShapedRecipes recipe = new ShapedRecipesBetweenlands(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, output);
//		registry.register(recipe);
//		return recipe;
//	}
//
//	public static IRecipe addShapelessRecipe(IForgeRegistry<IRecipe> registry, ItemStack output, Object... recipeComponents) {
//		NonNullList<ItemStack> list = NonNullList.create();
//
//		for (Object object : recipeComponents) {
//			if (object instanceof ItemStack) {
//				list.add(((ItemStack)object).copy());
//			} else if (object instanceof Item) {
//				list.add(new ItemStack((Item)object));
//			} else {
//				if (!(object instanceof Block)) {
//					throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
//				}
//
//				list.add(new ItemStack((Block)object));
//			}
//		}
//
//		ShapelessRecipes recipe = new ShapelessRecipesBetweenlands(output, list);
//		registry.register(recipe);
//		return recipe;
//	}
}
