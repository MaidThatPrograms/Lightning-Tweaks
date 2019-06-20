package caffeinatedpinkie.lightningtweaks;

import caffeinatedpinkie.lightningtweaks.common.AttributeMetallic;
import caffeinatedpinkie.lightningtweaks.common.Searcher;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Configuration file for the mod.
 *
 * @author CaffeinatedPinkie
 */
@Config(modid = LightningTweaks.MODID)
public class ConfigLT {
    /**
     * User defined whitelist and blacklist for metallic items.
     *
     * @author CaffeinatedPinkie
     * @see AttributeMetallic#setItems()
     */
    public static class ConfigMetallic {
	@Name("Item Blacklist")
	@Comment("If an item has a word from this list in its name, it will not be considered metallic. Overrides the whitelist. Case is ignored.")
	public String[] blacklist = new String[0];

	@Name("Max Level of Recursion")
	@Comment("How many levels in a recipe to check for metallic objects. For example, if only 'nugget' is searched for, 1 = Iron Nugget, 2 = Iron Nugget + Iron Ingot, 3 = Iron Nugget + Iron Ingot + Block of Iron, and 4 = Iron Nugget + Iron Ingot + Block of Iron + Anvil. After some point, increasing this will have no effect.")
	@RangeInt(min = 1)
	public int maxRecursion = 3;

	@Name("Item Whitelist")
	@Comment("If an item's name or one of an item's ingredients' name has a word from this list, the item will be considered metallic. Overriden by the blacklist. Case is ignored.")
	public String[] whitelist = { "ingot", "iron", "gold", "nugget" };
    }

    /**
     * {@link Block} score weight options subcategory.
     *
     * @author CaffeinatedPinkie
     * @see Searcher#score(BlockPos, Entity)
     */
    public static class ConfigWeight {
	@Name("Height Weight")
	@Comment("Specifies the weight for the height of a block when choosing one to strike.")
	@RangeDouble(min = 0, max = 1)
	public double heightWeight = .7;

	@Name("Block Material Weight")
	@Comment("Specifies the weight for the material of a block when choosing one to strike.")
	@RangeDouble(min = 0, max = 1)
	public double materialWeight = .2;

	@Name("Metal Armor Mutliplier")
	@Comment("Specifies the mutliplier of a block's score when an entity is wearing metal armor.")
	@RangeDouble(min = 0)
	public double metalArmorRatio = 1.25;

	@Name("Metal Item Mutliplier")
	@Comment("Specifies the mutliplier of a block's score when an entity is holding a metal item.")
	@RangeDouble(min = 0)
	public double metalItemRatio = 1.25;

	@Name("Non-Player Entity Mutliplier")
	@Comment("Specifies the mutliplier of a block's score when an entity is not a player.")
	@RangeDouble(min = 0)
	public double nonPlayerEntityRatio = 1;

	@Name("Player Mutliplier")
	@Comment("Specifies the mutliplier of a block's score when an entity is a player.")
	@RangeDouble(min = 0)
	public double playerEntityRatio = 1;

	@Name("Block Shape Weight")
	@Comment("Specifies the weight for the shape of a block when choosing one to strike.")
	@RangeDouble(min = 0, max = 1)
	public double shapeWeight = .1;
    }

    /**
     * Called when config values are changed.
     *
     * @author CaffeinatedPinkie
     */
    @Mod.EventBusSubscriber
    public static class EventHandler {
	@SubscribeEvent
	public static void onConfigChangedEvent(OnConfigChangedEvent event) {
	    if (event.getModID().equals(LightningTweaks.MODID)) {
		ConfigManager.sync(LightningTweaks.MODID, Type.INSTANCE);
		LightningTweaks.refresh();
	    }
	}
    }

    @Name("Metallic Items Options")
    @Comment("Options for what items should and shouldn't be considered metallic, based on their names.")
    public static ConfigMetallic metallic = new ConfigMetallic();

    @Name("Block Search Radius")
    @Comment("This is the radius that the mod will search within around the lightning. The center of the radius is based on the vanilla strike position. A higher radius will take more time to search.")
    @RangeInt(min = 0)
    public static int radius = 50;

    @Name("Random Strike Position?")
    @Comment("If true, lightning is not guaranteed to strike the block with the highest score. The score of each block will be used as a probability. If false, the block with the highest score will always be chosen.")
    public static boolean randomStrike = true;

    @Name("Verbose?")
    @Comment("If true, debug messages will be sent to the client or server console.")
    public static boolean verbose = false;

    @Name("Search Weight Options")
    @Comment("Options for the weights that the block scoring system uses. Scores should be between 0 and 1 on default settings, without multipliers.")
    public static ConfigWeight weight = new ConfigWeight();
}
