package caffeinatedpinkie.lightningtweaks.common;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import caffeinatedpinkie.lightningtweaks.ConfigLT;
import caffeinatedpinkie.lightningtweaks.ConfigLT.ConfigMetallic;
import caffeinatedpinkie.lightningtweaks.LoggerLT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Contains fields and methods that determine if a specified {@link IBlockState}
 * or {@link ItemStack} can be considered metallic.
 *
 * @author CaffeinatedPinkie
 */
public class AttributeMetallic {
    public static Set<String> items = new HashSet<>(), visited = new HashSet<>();
    public static Multimap<String, String> recipes = HashMultimap.create();

    /**
     * Determines if the given {@link String} contains any of the {@link String}s in
     * the given array, ignoring case.
     *
     * @param searchStrings an array of {@link String}s to run
     *                      {@link StringUtils#containsIgnoreCase(CharSequence, CharSequence)}
     *                      against
     * @param str           the {@link String} to search within
     * @return true if any one {@link String} from the given array are contained
     *         within the given {@link String}, ignoring case, false if none do
     */
    public static boolean containsIgnoreCase(String[] searchStrings, String str) {
	for (String searchStr : searchStrings)
	    if (StringUtils.containsIgnoreCase(str, searchStr))
		return true;
	return false;
    }

    /**
     * Attempts to retrieve a unique {@link String} identifier for the given
     * {@link IBlockState} by forwarding it to {@link #getIdentifier(ItemStack)}
     *
     * @param state the {@link IBlockState} to retrieve the name of
     * @return a {@link String} representing the given {@link IBlockState} that is
     *         unique
     */
    public static String getIdentifier(IBlockState state) {
	return getIdentifier(new ItemStack(state.getBlock()));
    }

    /**
     * Attempts to retrieve a unique {@link String} identifier for the given
     * {@link Item} by forwarding it to {@link #getIdentifier(ItemStack)}
     *
     * @param item the {@link Item} to retrieve the name of
     * @return a {@link String} representing the given {@link Item} that is unique
     */
    public static String getIdentifier(Item item) {
	return getIdentifier(new ItemStack(item));
    }

    /**
     * Attempts to retrieve a unique {@link String} identifier for the given
     * {@link ItemStack}. Has a fallback mechanism in direct response to an issue
     * with Industrial Craft 2 which first calls
     * {@link ItemStack#getTranslationKey()}, then
     * {@link ItemStack#getDisplayName()} or {@link ItemStack#toString()}, if
     * {@link ItemStack#hasDisplayName} returns false.
     *
     * @param stack the {@link ItemStack} to retrieve the name of
     * @return a {@link String} representing the given {@link ItemStack} that is
     *         unique
     */
    public static String getIdentifier(ItemStack stack) {
	try {
	    return stack.getTranslationKey();
	} catch (Exception e) {
	    LoggerLT.warn("Error while obtaining translation key for item stack: " + stack
		    + ". Falling back on display name or toString() if there is none.", e);
	    return stack.hasDisplayName() ? stack.getDisplayName() : stack.toString();
	}
    }

    /**
     * Searches {@link #items} to see if the given {@link IBlockState} is contained
     * and therefore metallic.
     *
     * @param state the {@link IBlockState} that may or may not be metallic
     * @return true if the {@link IBlockState} is metallic, false otherwise
     */
    public static boolean isMetallic(IBlockState state) {
	return items.contains(getIdentifier(state));
    }

    /**
     * Searches {@link #items} to see if the given {@link ItemStack} is contained
     * and therefore metallic.
     *
     * @param stack the {@link ItemStack} that may or may not be metallic
     * @return true if the {@link ItemStack} is metallic, false otherwise
     */
    public static boolean isMetallic(ItemStack stack) {
	return items.contains(getIdentifier(stack));
    }

    /**
     * Called by the main mod class when Forge is in its post-initialization phase.
     * Calls {@link #setRecipes()} and {@link #setItems()}.
     *
     * @param event the {@link FMLPostInitializationEvent}
     */
    public static void postInit(FMLPostInitializationEvent event) {
	setRecipes();
	setItems();
    }

    /**
     * Recursively registers the given translation key in {@link #items} and
     * attempts to register any {@link IRecipe}s it is used in based on
     * {@link #recipes}. The base case is when either the passed level is greater
     * than or equal to {@link ConfigMetallic#maxRecursion} or if the given
     * {@link Set} contains the given translation key already. The level is
     * incremented by one on each method call.<br>
     * <br>
     * The recursion level prevents items with hardly any metal from being
     * registered, say from a recipe ten layers deep. The {@link Set} prevents
     * infinite loops from {@link IRecipe}s that reflect each other or trying a
     * {@link IRecipe} an unnecessary amount of times.
     *
     * @param itemKey the translation key of an {@link ItemStack} to be registered
     *                with {@link #items}
     * @param level   the current level of recursion the method is running at
     * @param visited contains every translation key already visited in this
     *                recursive tree
     */
    public static void registerMetallic(String itemKey, int level, Set<String> visited) {
	if (level < ConfigLT.metallic.maxRecursion && visited.add(itemKey)) {
	    items.add(itemKey);
	    recipes.get(itemKey).forEach(outputKey -> registerMetallic(outputKey, level + 1, visited));
	}
    }

    /**
     * Checks each {@link Item} in {@link ForgeRegistries#ITEMS} against
     * {@link ConfigMetallic#whitelist} and {@link ConfigMetallic#blacklist}. If the
     * {@link Item}'s translation key is in {@link ConfigMetallic#whitelist} and not
     * in {@link ConfigMetallic#blacklist},
     * {@link #registerMetallic(String, int, Set)} is called. Should be called
     * whenever any property in {@link ConfigMetallic} is changed and during
     * initialization.
     */
    public static void setItems() {
	LoggerLT.log(() -> {
	    items.clear();
	    ForgeRegistries.ITEMS.forEach(item -> {
		String itemKey = getIdentifier(item);
		if (!containsIgnoreCase(ConfigLT.metallic.blacklist, itemKey)
			&& containsIgnoreCase(ConfigLT.metallic.whitelist, itemKey)) {
		    visited.clear();
		    registerMetallic(itemKey, 0, visited);
		}
	    });
	}, "Metallic item list populated");
	LoggerLT.log("Found " + items.size() + " items: " + items);
    }

    /**
     * Adds each {@link IRecipe} in {@link ForgeRegistries#RECIPES} to
     * {@link #recipes} as a value, with its {@link Ingredient}s as separate keys.
     * Only necessary to call during initialization.
     */
    public static void setRecipes() {
	LoggerLT.log(() -> {
	    recipes.clear();
	    ForgeRegistries.RECIPES.forEach(recipe -> {
		ItemStack outputStack = recipe.getRecipeOutput();
		if (!outputStack.isEmpty()) {
		    String outputKey = getIdentifier(outputStack);
		    for (Ingredient ingredient : recipe.getIngredients())
			for (ItemStack matchingStack : ingredient.getMatchingStacks())
			    recipes.put(getIdentifier(matchingStack), outputKey);
		}
	    });
	}, "Recipe list populated");
    }
}
