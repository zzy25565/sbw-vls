package com.zzy.sbwvls;

import com.atsuishio.superbwarfare.item.container.ContainerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * Creative tabs split by vehicle category (tank/apc/aa/airplane/helicopter/
 * car/artillery/defense/boat). Replaces the old single-list dump into the
 * SBW unified vehicles tab (assembly table does not consume CONTAINERS,
 * verified against SBW 0.8.9.1: only ModTabs reads it).
 */
public final class CreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SbwVlsMod.MODID);

    private record Category(String name, String icon, List<String> ids) {}

    private static final List<Category> CATEGORIES = new ArrayList<>();

    private static void cat(String name, String icon, String... ids) {
        CATEGORIES.add(new Category(name, icon, List.of(ids)));
    }

    static {
        cat("tank", "ztz99a", "2s25m", "abrams_x", "amx56", "challenger_ds", "chonmaho5", "cyborg_tank", "darkbear", "hyr0", "k1a1", "leopard2a4", "m10booker", "m1a1", "m1a1hc", "m1a2sepv1", "m1a2sepv2", "m808", "markv", "project640", "pzbjy", "rail_tank", "silverhand_mk_iv", "strv103", "t72b", "t72b3", "t80", "t80b", "t90_msa", "t90_msa_keep", "t90mh", "test", "vt4a1", "vt4b", "ztq15", "ztz59a", "ztz96a", "ztz99a", "ztz99ah", "ztz99bh");
        cat("apc", "2s38", "aav7a1", "aavc7c1", "bmd4", "bmpt72", "brdm2", "btr82a", "cm34", "cv90", "m1126", "m1128", "m113", "m1296", "zbd04a", "zbd05", "zbl08", "zlt11", "zsl10", "ztd05");
        cat("aa", "buk_m3", "625e", "akm", "buk_m3", "flarakpz1", "l1a2", "tunguska", "zsu_23_4");
        cat("airplane", "j20", "ac130", "av8b", "camel", "f14", "f15e", "f16c", "fa18e", "ik_fightcat", "ik_locust", "j10", "j10c", "j11", "j15t", "j16", "j20", "j20vtol", "j35", "j8", "jas39e", "jf17", "q5", "refale", "su24m", "syy651", "t77h", "tjgc");
        cat("helicopter", "z20", "ah1f", "ah64", "ec665", "ka50", "mi24p", "mi24v", "nh90", "sd905", "test_ship", "uh144", "uh60", "z10a", "z10me", "z20", "z9");
        cat("car", "humvee", "cobra2", "csk181", "fav_a", "humvee", "humveetow", "kozak5", "m12", "m12gau", "m12hmg", "m12roc", "m12tra", "m274", "m274m", "motuo", "mv3", "mv3_armed", "mv3_supply", "sx1", "toyota_seiki", "ural4320", "ural4320_supply", "ural4320_zu23", "wlhgzu23", "wlsc", "z_sts_akhmat");
        cat("artillery", "vls_cruise_24", "bkan1c", "gvozdika_2s1", "m270", "vls_ballistic_16", "vls_ballistic_8", "vls_cruise_12", "vls_cruise_24", "vls_turret");
        cat("defense", "9m133", "9m133", "hj8", "mk19", "npds114", "npds514", "npds810", "qjz89", "shield", "spg9", "zu23");


        for (Category c : CATEGORIES) {
            TABS.register(c.name(), () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sbw_vls." + c.name()))
                    .icon(() -> stackFor(c.icon()))
                    .displayItems((params, out) -> {
                        for (String id : c.ids()) {
                            ItemStack s = stackFor(id);
                            if (!s.isEmpty()) out.accept(s);
                        }
                    })
                    .build());
        }
    }

    public static ItemStack stackFor(String id) {
        EntityType<?> t = entityTypeFor(id);
        if (t == null) return ItemStack.EMPTY;
        try {
            ItemStack s = ContainerBlockItem.Companion.createInstance(t);
            return s == null ? ItemStack.EMPTY : s;
        } catch (Throwable e) {
            System.out.println("[sbw_vls] container item failed: " + id + " " + e);
            return ItemStack.EMPTY;
        }
    }

    private static EntityType<?> entityTypeFor(String id) {
        EntityType<?> t = MergedVehicles.TYPES.get(id);
        if (t != null) return t;
        return switch (id) {
            case "vls_turret" -> VlsEntities.VLS_TURRET;
            case "buk_m3" -> VlsEntities.BUK_M3;
            case "vls_cruise_12" -> VlsEntities.VLS_CRUISE_12;
            case "vls_cruise_24" -> VlsEntities.VLS_CRUISE_24;
            case "vls_ballistic_8" -> VlsEntities.VLS_BALLISTIC_8;
            case "vls_ballistic_16" -> VlsEntities.VLS_BALLISTIC_16;
            default -> null;
        };
    }

    private CreativeTabs() {}
}
