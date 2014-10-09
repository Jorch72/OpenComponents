package li.cil.occ.mods.vanilla;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.occ.mods.ManagedTileEntityEnvironment;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.World;

public final class DriverBeacon extends DriverTileEntity implements NamedBlock {
    @Override
    public Class<?> getTileEntityClass() {
        return TileEntityBeacon.class;
    }

    @Override
    public String preferredName() {
        return "beacon";
    }

    @Override
    public ManagedEnvironment createEnvironment(final World world, final int x, final int y, final int z) {
        return new Environment((TileEntityBeacon) world.getTileEntity(x, y, z));
    }

    public static final class Environment extends ManagedTileEntityEnvironment<TileEntityBeacon> {
        public Environment(final TileEntityBeacon tileEntity) {
            super(tileEntity, "beacon");
        }

        @Callback(doc = "function():number -- Get the number of levels for this beacon.")
        public Object[] getLevels(final Context context, final Arguments args) {
            return new Object[]{tileEntity.getLevels()};
        }

        @Callback(doc = "function():string -- Get the name of the active primary effect.")
        public Object[] getPrimaryEffect(final Context context, final Arguments args) {
            return new Object[]{getEffectName(tileEntity.getPrimaryEffect())};
        }

        @Callback(doc = "function():string -- Get the name of the active secondary effect.")
        public Object[] getSecondayEffect(final Context context, final Arguments args) {
            return new Object[]{getEffectName(tileEntity.getSecondaryEffect())};
        }
    }

    private static String getEffectName(final int id) {
        return (id >= 0 && id < Potion.potionTypes.length && Potion.potionTypes[id] != null)
                ? Potion.potionTypes[id].getName()
                : null;
    }
}
