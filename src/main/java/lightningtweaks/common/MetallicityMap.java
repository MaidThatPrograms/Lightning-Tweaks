package lightningtweaks.common;

import java.util.HashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.common.Tags;

/**
 * TODO
 */
public class MetallicityMap extends HashMap<Item, Double> {
	/**
	 * TODO
	 *
	 * @param manager TODO
	 */
	public MetallicityMap(RecipeManager manager) {
		Tags.Items.NUGGETS.getAllElements().forEach(nugget -> put(nugget, 1D));
		Multimap<Item, IRecipe<?>> recipeMap = HashMultimap.create();
		manager.getRecipes().forEach(recipe -> recipeMap.put(recipe.getRecipeOutput().getItem(), recipe));
		recipeMap.keySet().forEach(item -> get(item, recipeMap));
	}

	/**
	 * TODO
	 *
	 * @param ingredient TODO
	 * @param recipes    TODO
	 * @return TODO
	 */
	private double[] get(Ingredient ingredient, Multimap<Item, IRecipe<?>> recipes) {
		double metallicity = 0;
		int count = 0;
		ItemStack[] stacks = ingredient.getMatchingStacks();
		for (ItemStack stack : stacks) {
			metallicity += get(stack.getItem(), recipes);
			count += stack.getCount();
		}
		return new double[] { metallicity, (double) count / stacks.length };
	}

	/**
	 * TODO
	 *
	 * @param recipe  TODO
	 * @param recipes TODO
	 * @return TODO
	 */
	private double get(IRecipe<?> recipe, Multimap<Item, IRecipe<?>> recipes) {
		double metallicity = 0;
		double count = 0;
		for (Ingredient ingredient : recipe.getIngredients())
			if (!ingredient.hasNoMatchingItems()) {
				double[] array = get(ingredient, recipes);
				metallicity += array[0];
				count += array[1];
			}
		return metallicity / count;
	}

	/**
	 * TODO
	 *
	 * @param item    TODO
	 * @param recipes TODO
	 * @return TODO
	 */
	private double get(Item item, Multimap<Item, IRecipe<?>> recipes) {
		if (putIfAbsent(item, 0.0) == null) {
			double metallicity = 0;
			for (IRecipe<?> recipe : recipes.get(item))
				metallicity = Math.max(metallicity, get(recipe, recipes));
			put(item, metallicity);
		}
		return get(item);
	}
}