package lightningtweaks.common;

import lightningtweaks.LightningTweaks;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;

@Config(modid = LightningTweaks.MODID)
public class LTConfig {
    @Ignore
    private static final String lightningColorName = "lightning bolt color";

    @Name(lightningColorName)
    @Comment("Sets the color and opacity of lightning bolts!")
    public static ConfigColor lightningColor = new ConfigColor();

    @Name("Spawn Lightning Fire?")
    @Comment("Determines whether lightning bolts will spawn fire where they strike. Does not prevent fire from other sources.\n\nThis option is overridden by disabling fire in the world or spawning lightning as an effect only. Only applies to vanilla lightning.\n\nThis option is really only useful for disabling vanilla lightning fire specifically.")
    public static boolean fire = true;

    public static String getLightningColorName() {
	return lightningColorName;
    }
}