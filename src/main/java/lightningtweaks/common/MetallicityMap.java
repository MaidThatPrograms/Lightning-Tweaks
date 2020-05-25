package lightningtweaks.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

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
public class MetallicityMap {
	private static final Map<Item, Double> map = new HashMap<>();

	/**
	 * TODO
	 *
	 * @param ingredient TODO
	 * @param recipes    TODO
	 * @return TODO
	 */
	private static Pair<Double, Double> get(Ingredient ingredient, Multimap<Item, IRecipe<?>> recipes) {
		double metallicity = 0;
		int count = 0;
		ItemStack[] stacks = ingredient.getMatchingStacks();
		for (ItemStack stack : stacks) {
			metallicity += get(stack.getItem(), recipes);
			count += stack.getCount();
		}
		return Pair.of(metallicity, (double) count / stacks.length);
	}

	/**
	 * TODO
	 *
	 * @param recipe  TODO
	 * @param recipes TODO
	 * @return TODO
	 */
	private static double get(IRecipe<?> recipe, Multimap<Item, IRecipe<?>> recipes) {
		double metallicity = 0;
		double count = 0;
		for (Ingredient ingredient : recipe.getIngredients())
			if (!ingredient.hasNoMatchingItems()) {
				Pair<Double, Double> pair = get(ingredient, recipes);
				metallicity += pair.getLeft();
				count += pair.getRight();
			}
		return metallicity / count;
	}

	/**
	 * TODO
	 *
	 * @param item TODO
	 * @return TODO
	 */
	public static double get(Item item) {
		return map.getOrDefault(item, 0.0);
	}

	/**
	 * TODO
	 *
	 * @param item    TODO
	 * @param recipes TODO
	 * @return TODO
	 */
	private static double get(Item item, Multimap<Item, IRecipe<?>> recipes) {
		if (map.putIfAbsent(item, 0.0) == null) {
			double metallicity = 0;
			for (IRecipe<?> recipe : recipes.get(item))
				metallicity = Math.max(metallicity, get(recipe, recipes));
			map.put(item, metallicity);
		}
		return map.get(item);
	}

	/**
	 * TODO
	 *
	 * @param manager TODO
	 */
	public static void update(RecipeManager manager) {
		Tags.Items.NUGGETS.getAllElements().forEach(nugget -> map.put(nugget, 1.0));
		Multimap<Item, IRecipe<?>> recipeMap = HashMultimap.create();
		manager.getRecipes().forEach(recipe -> recipeMap.put(recipe.getRecipeOutput().getItem(), recipe));
		recipeMap.keySet().forEach(item -> get(item, recipeMap));
	}
}