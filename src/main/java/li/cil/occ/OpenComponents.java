package li.cil.occ;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import li.cil.occ.mods.Registry;
import li.cil.occ.mods.appeng.ModAppEng;
import li.cil.occ.mods.buildcraft.ModBuildCraft;
import li.cil.occ.mods.cofh.energy.ModCoFHEnergy;
import li.cil.occ.mods.cofh.tileentity.ModCoFHTileEntity;
import li.cil.occ.mods.cofh.transport.ModCoFHTransport;
import li.cil.occ.mods.computercraft.ModComputerCraft;
import li.cil.occ.mods.enderio.ModEnderIO;
import li.cil.occ.mods.enderstorage.ModEnderStorage;
import li.cil.occ.mods.forestry.ModForestry;
import li.cil.occ.mods.gregtech.ModGregtech;
import li.cil.occ.mods.ic2.ModIndustrialCraft2;
import li.cil.occ.mods.mystcraft.ModMystcraft;
import li.cil.occ.mods.railcraft.ModRailcraft;
import li.cil.occ.mods.thaumcraft.ModThaumcraft;
import li.cil.occ.mods.thermalexpansion.ModThermalExpansion;
import li.cil.occ.mods.tmechworks.ModTMechworks;
import li.cil.occ.mods.vanilla.ModVanilla;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod(modid = OpenComponents.ID, name = OpenComponents.Name, version = OpenComponents.Version, useMetadata = true)
public class OpenComponents {
    public static final String ID = "OpenComponents";

    public static final String Name = "OpenComponents";

    public static final String Version = "@VERSION@";

    @Mod.Instance
    public static OpenComponents Instance;

    public static final Logger Log = LogManager.getLogger(ID);

    public static final Set<Runnable> ScheduledTicks = new HashSet<Runnable>();

    public static final ExecutorService Executor = Executors.newCachedThreadPool();

    public static String[] modBlacklist = new String[]{
            ModThaumcraft.MOD_ID
    };

    public static String[] peripheralBlacklist = new String[]{
            "net.minecraft.tileentity.TileEntityCommandBlock"
    };

    public static Boolean allowItemStackInspection = false;

    public static String fakePlayerUuid = "7e506b5d-2ccb-4ac4-a249-5624925b0c67";

    public static String fakePlayerName = "[OpenComponents]";

    public static GameProfile fakePlayerProfile;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent e) {
        final Configuration config = new Configuration(e.getSuggestedConfigurationFile());

        modBlacklist = config.get("mods", "blacklist", modBlacklist, "" +
                "A list of mods (by mod id) for which support should NOT be\n" +
                "enabled. Use this to disable support for mods you feel should\n" +
                "not be controllable via computers (such as magic related mods,\n" +
                "which is why Thaumcraft is on this list by default.)").
                getStringList();

        peripheralBlacklist = config.get("computercraft", "blacklist", peripheralBlacklist, "" +
                "A list of tile entities by class name that should NOT be\n" +
                "accessible via the Adapter block. Add blocks here that can\n" +
                "lead to crashes or deadlocks (and report them, please!)").
                getStringList();

        allowItemStackInspection = config.get("vanilla", "allowItemStackInspection", false).
                getBoolean(false);

        fakePlayerUuid = config.get("general", "fakePlayerUuid", fakePlayerUuid).
                getString();

        fakePlayerName = config.get("general", "fakePlayerName", fakePlayerName).
                getString();

        fakePlayerProfile = new GameProfile(UUID.fromString(fakePlayerUuid), fakePlayerName);

        config.save();
    }

    @Mod.EventHandler
    public void init(final FMLInitializationEvent e) {
        Registry.add(new ModAppEng());
        Registry.add(new ModBuildCraft());
        Registry.add(new ModCoFHEnergy());
        Registry.add(new ModCoFHTileEntity());
        Registry.add(new ModCoFHTransport());
        Registry.add(new ModEnderIO());
        Registry.add(new ModEnderStorage());
        Registry.add(new ModForestry());
        Registry.add(new ModGregtech());
        Registry.add(new ModIndustrialCraft2());
        Registry.add(new ModMystcraft());
        Registry.add(new ModRailcraft());
        Registry.add(new ModThaumcraft());
        Registry.add(new ModThermalExpansion());
        Registry.add(new ModTMechworks());
        Registry.add(new ModVanilla());

        // Register the general IPeripheral driver last, if at all, to avoid it
        // being used rather than other more concrete implementations, such as
        // is the case in the Redstone in Motion driver (replaces 'move').
        Registry.add(new ModComputerCraft());

        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ServerTickEvent e) {
        synchronized (ScheduledTicks) {
            for (final Runnable handler : ScheduledTicks) {
                handler.run();
            }
            ScheduledTicks.clear();
        }
    }

    public static void schedule(final Runnable handler) {
        synchronized (ScheduledTicks) {
            ScheduledTicks.add(handler);
        }
    }
}
