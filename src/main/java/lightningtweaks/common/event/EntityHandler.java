package lightningtweaks.common.event;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EntityHandler {
    private static final Set<World> map = new HashSet<>();
    private static final String fireRule = "doFireTick";

    @SubscribeEvent
    public static void onConstructing(EntityConstructing event) {
	Entity entity = event.getEntity();
	if (entity instanceof EntityWeatherEffect) {
	    World world = entity.getEntityWorld();
	    if (!world.isRemote) {
		GameRules gameRules = world.getGameRules();
		if (gameRules.getBoolean(fireRule)) {
		    map.add(world);
		    setFireRule(gameRules, false);
		}
	    }
	}
    }

    public static boolean pollFireRule(World world) {
	return map.remove(world);
    }

    public static void setFireRule(GameRules gameRules, boolean ruleValue) {
	gameRules.setOrCreateGameRule(fireRule, String.valueOf(ruleValue));
    }
}