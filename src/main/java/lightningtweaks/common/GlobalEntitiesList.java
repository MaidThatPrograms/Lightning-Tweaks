package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import lightningtweaks.common.event.EntityHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;

/**
 * Modified version of an {@link ArrayList} of type {@link Entity}. The only
 * behavior that is changed is {@link #add(Entity)} to catch the given
 * {@link Entity} and apply this mod's logic to it.
 */
public class GlobalEntitiesList extends ArrayList<Entity> {
    private static final SplittableRandom random = new SplittableRandom();
    private final IWorld world;

    /**
     * Constructs an instance of {@link GlobalEntitiesList} on an instance of
     * {@link IWorld}. The given {@link IWorld} should be the same instance of
     * {@link IWorld} that this {@link GlobalEntitiesList} is applied to. This also
     * means that the {@link IWorld} should be an instance of {@link ServerWorld}.
     * <hr>
     * <b>Basically, the following statements should be true:</b>
     * <ul>
     * <li>world instanceof {@link ServerWorld}</li>
     * <li>(({@link ServerWorld}) world).{@link ServerWorld#globalEntities
     * globalEntities} == {@link GlobalEntitiesList this}</li>
     * </ul>
     *
     * @param world the {@link IWorld} that this {@link GlobalEntitiesList} is
     *              applied to
     */
    public GlobalEntitiesList(IWorld world) {
	this.world = world;
    }

    /**
     * {@inheritDoc}
     * <hr>
     * Beyond the normal functionality of {@link ArrayList#add(Object)}, this method
     * also calls {@link EntityHandler#revertDifficulty(IWorld)},
     * {@link #getMaxBlockPos(Entity)}, and finally
     * {@link Entity#setPosition(double, double, double) sets the position} of the
     * given {@link Entity} to the returned {@link BlockPos}. <br>
     * <br>
     * There is no check that the given {@link Entity} is an instance of
     * {@link LightningBoltEntity} because it appears that
     * {@link LightningBoltEntity} is the only thing ever passed into this method.
     * If this ever ceases to be the case or another mod passes something else in, a
     * check will be added.<br>
     * <br>
     */
    @Override
    public boolean add(Entity e) {
	EntityHandler.revertDifficulty(world);
	BlockPos pos = getMaxBlockPos(e);
	e.setPosition(pos.getX(), pos.getY(), pos.getZ());
	return super.add(e);
    }

    /**
     * Finds the highest {@link Block} or {@link Block}s in the {@link IChunk} of
     * the given {@link Entity}. This is done by accessing the {@link IChunk}'s
     * {@link Heightmap} of {@link Type#MOTION_BLOCKING}. That {@link Type} is used
     * to match the vanilla method
     * {@link ServerWorld#adjustPosToNearbyEntity(BlockPos)} which used before
     * naturally spawning {@link LightningBoltEntity LightningBoltEntities}. If
     * multiple {@link BlockPos}es are equally the highest, one is chosen randomly
     * using {@link SplittableRandom#nextInt(int)}.
     *
     * @param e the {@link Entity} whose {@link IChunk} will be searched
     * @return the {@link BlockPos} of one of the highest {@link Block}s in the
     *         given {@link Entity}'s {@link IChunk}, according to
     *         {@link Type#MOTION_BLOCKING}
     */
    private BlockPos getMaxBlockPos(Entity e) {
	IChunk chunk = world.getChunk(e.getPosition());
	Heightmap heightmap = chunk.getHeightmap(Type.MOTION_BLOCKING);
	int max = 0;
	List<int[]> coords = new ArrayList<>();
	for (int x = 0; x < 16; x++)
	    for (int z = 0; z < 16; z++) {
		int y = heightmap.getHeight(x, z);
		if (y > max) {
		    max = y;
		    coords.clear();
		}
		if (y == max)
		    coords.add(new int[] { x, z });
	    }
	int[] coord = coords.get(random.nextInt(coords.size()));
	return chunk.getPos().getBlock(coord[0], max, coord[1]);
    }
}
