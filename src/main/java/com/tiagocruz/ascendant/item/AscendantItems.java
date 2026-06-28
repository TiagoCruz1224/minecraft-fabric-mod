package com.tiagocruz.ascendant.item;

import com.tiagocruz.ascendant.Ascendant;
import com.tiagocruz.ascendant.data.PlayerClass;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * Todos os itens do mod Ascendant.
 * Em 1.21.4 é obrigatório definir o ResourceKey nas Properties ANTES de construir o Item.
 * Usamos o helper register(name, factory) que injeta as props com ID já definido.
 */
public class AscendantItems {

    // ── ARMAS ────────────────────────────────────────────────────────────────

    public static final ClassWeapon SHADOW_DAGGER = register("shadow_dagger",
        props -> new ClassWeapon(PlayerClass.ASSASSIN,
            "Adaga das Sombras",
            "Forjada na obscuridade, corta sem ser vista.",
            props));

    public static final ClassWeapon BASTION_MACE = register("bastion_mace",
        props -> new ClassWeapon(PlayerClass.GUARDIAN,
            "Maça do Baluarte",
            "Cada golpe é uma muralha. Lenta mas imparável.",
            props));

    public static final ClassWeapon ARCANE_STAFF = register("arcane_staff",
        props -> new ClassWeapon(PlayerClass.MAGE,
            "Cajado Arcano",
            "Canaliza a energia arcana em feixe de poder.",
            props));

    public static final ClassWeapon TITAN_HAMMER = register("titan_hammer",
        props -> new ClassWeapon(PlayerClass.TITAN,
            "Martelo do Titã",
            "Tão pesado que o chão treme a cada impacto.",
            props));

    public static final ClassWeapon HUNTER_BOW = register("hunter_bow",
        props -> new ClassWeapon(PlayerClass.ARCHER,
            "Arco do Caçador",
            "Precisão sobrenatural. O alvo nunca escapa.",
            props));

    public static final ClassWeapon LIFE_SCEPTER = register("life_scepter",
        props -> new ClassWeapon(PlayerClass.HEALER,
            "Cetro da Vida",
            "Cada toque drena vida para restaurar aliados.",
            props));

    public static final ClassWeapon SHADOW_TOME = register("shadow_tome",
        props -> new ClassWeapon(PlayerClass.SUMMONER,
            "Tomo das Sombras",
            "Palavras proibidas que convocam o inominável.",
            props));

    public static final ClassWeapon SPECTRAL_BLADE = register("spectral_blade",
        props -> new ClassWeapon(PlayerClass.SPECTER,
            "Lâmina Espectral",
            "Atravessa armaduras como se não existissem.",
            props));

    // ── ARMADURAS ────────────────────────────────────────────────────────────

    public static final ClassArmor SHADOW_CLOAK = register("shadow_cloak",
        props -> new ClassArmor(PlayerClass.ASSASSIN,
            "Capote das Sombras",
            "Tecido com trevas concentradas. Reduz silhueta.", 4,
            props));

    public static final ClassArmor BASTION_PLATE = register("bastion_plate",
        props -> new ClassArmor(PlayerClass.GUARDIAN,
            "Armadura do Baluarte",
            "Nenhum golpe vai a fundo. Lenta mas impassível.", 10,
            props));

    public static final ClassArmor ARCANE_ROBE = register("arcane_robe",
        props -> new ClassArmor(PlayerClass.MAGE,
            "Manto Arcano",
            "Fraco ao impacto, mas amplia drasticamente a magia.", 2,
            props));

    public static final ClassArmor TITAN_PLATE = register("titan_plate",
        props -> new ClassArmor(PlayerClass.TITAN,
            "Corselete do Titã",
            "Pesa mais do que um bloco de obsidiana.", 12,
            props));

    public static final ClassArmor HUNTER_VEST = register("hunter_vest",
        props -> new ClassArmor(PlayerClass.ARCHER,
            "Colete do Caçador",
            "Leve e flexível. Não atrapalha os movimentos.", 5,
            props));

    public static final ClassArmor HEALER_VESTMENT = register("healer_vestment",
        props -> new ClassArmor(PlayerClass.HEALER,
            "Vestes Sagradas",
            "Abençoadas. Aceleram a regeneração de quem as usa.", 3,
            props));

    public static final ClassArmor SUMMONER_ROBE = register("summoner_robe",
        props -> new ClassArmor(PlayerClass.SUMMONER,
            "Manto Invocador",
            "Liga o portador ao reino espiritual.", 3,
            props));

    public static final ClassArmor SPECTER_CLOAK = register("specter_cloak",
        props -> new ClassArmor(PlayerClass.SPECTER,
            "Capa Espectral",
            "Quase imaterial. O portador parece não estar lá.", 4,
            props));

    // ── CREATIVE TAB ─────────────────────────────────────────────────────────

    public static final ResourceKey<CreativeModeTab> ASCENDANT_TAB =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, Ascendant.id("ascendant"));

    public static void register() {
        // Registar creative tab
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ASCENDANT_TAB,
            FabricItemGroup.builder()
                .title(Component.literal("§6[Ascendant]"))
                .icon(() -> new ItemStack(SHADOW_DAGGER))
                .displayItems((params, output) -> {
                    output.accept(SHADOW_DAGGER);
                    output.accept(BASTION_MACE);
                    output.accept(ARCANE_STAFF);
                    output.accept(TITAN_HAMMER);
                    output.accept(HUNTER_BOW);
                    output.accept(LIFE_SCEPTER);
                    output.accept(SHADOW_TOME);
                    output.accept(SPECTRAL_BLADE);
                    output.accept(SHADOW_CLOAK);
                    output.accept(BASTION_PLATE);
                    output.accept(ARCANE_ROBE);
                    output.accept(TITAN_PLATE);
                    output.accept(HUNTER_VEST);
                    output.accept(HEALER_VESTMENT);
                    output.accept(SUMMONER_ROBE);
                    output.accept(SPECTER_CLOAK);
                })
                .build()
        );
        Ascendant.LOGGER.info("[Ascendant] {} itens registados.", 16);
    }

    // Helper: cria props com ResourceKey já definido e regista o item
    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Ascendant.id(name));
        T item = factory.apply(new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
}
