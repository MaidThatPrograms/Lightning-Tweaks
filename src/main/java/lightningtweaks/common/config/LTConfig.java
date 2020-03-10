package lightningtweaks.common.config;

import lightningtweaks.LightningTweaks;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;

@Config(modid = LightningTweaks.MODID)
public class LTConfig {
    @Ignore
    public static final String LIGHTNING_COLOR_NAME = "lightning bolt color";

    @Name(LIGHTNING_COLOR_NAME)
    @Comment("TODO")
    public static ConfigColor lightningColor = new ConfigColor();
}