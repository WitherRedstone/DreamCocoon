package com.chinaex123.dream_cocoon.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DCIServerConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue DREAM_BAG_CHANCE;
    public static final ModConfigSpec.DoubleValue SWEET_BAG_CHANCE;
    public static final ModConfigSpec.DoubleValue GOODIE_BAG_CHANCE;

    public static final ModConfigSpec.IntValue DREAM_BAG_AMOUNT;
    public static final ModConfigSpec.IntValue SWEET_BAG_AMOUNT;
    public static final ModConfigSpec.IntValue GOODIE_BAG_AMOUNT;

    public static final ModConfigSpec.DoubleValue BOOSTED_DREAM_BAG_CHANCE;
    public static final ModConfigSpec.DoubleValue BOOSTED_SWEET_BAG_CHANCE;
    public static final ModConfigSpec.DoubleValue BOOSTED_GOODIE_BAG_CHANCE;

    public static final ModConfigSpec.IntValue DREAM_BAG_LOOT_AMOUNT;
    public static final ModConfigSpec.IntValue SWEET_BAG_LOOT_AMOUNT;
    public static final ModConfigSpec.IntValue GOODIE_BAG_LOOT_AMOUNT;

    public static final ModConfigSpec.IntValue QUALITY_BOOST_THRESHOLD;
    public static final ModConfigSpec.IntValue MAX_STREAK_THRESHOLD;


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("好梦包").push("Goodie Bag");
        GOODIE_BAG_CHANCE = builder
                .comment("获得好梦包的概率")
                .comment("Probability of receiving Goodie Bag when boosted")
                .defineInRange("goodieBagChance", 0.7, 0.0, 1.0);
        GOODIE_BAG_AMOUNT = builder
                .comment("获得好梦包的数量")
                .comment("Amount of Goodie Bag received")
                .defineInRange("goodieBagAmount", 1, 1, Integer.MAX_VALUE);
        BOOSTED_GOODIE_BAG_CHANCE = builder
                .comment("品质提升后给予好梦包的概率")
                .comment("Chance to grant a Goodie Bag bundle after quality upgrade")
                .defineInRange("boostedGoodieBagChance", 0.3, 0.0, 1.0);
        GOODIE_BAG_LOOT_AMOUNT = builder
                .comment("好梦包开出物品的数量")
                .comment("Number of items obtained from the Goodie Bag bundle")
                .defineInRange("goodieBagLootAmount", 1, 1, Integer.MAX_VALUE);

        builder.comment("甜梦包").push("Sweet Bag");
        SWEET_BAG_CHANCE = builder
                .comment("获得甜梦包的概率")
                .comment("Probability of receiving Sweet Bag when boosted")
                .defineInRange("sweetBagChance", 0.25, 0.0, 1.0);
        SWEET_BAG_AMOUNT = builder
                .comment("获得甜梦包的数量")
                .comment("Amount of Sweet Bag received")
                .defineInRange("sweetBagAmount", 1, 1, Integer.MAX_VALUE);
        BOOSTED_SWEET_BAG_CHANCE = builder
                .comment("品质提升后给予甜梦包的概率")
                .comment("Chance to grant a Sweet Bag bundle after quality upgrade")
                .defineInRange("boostedSweetBagChance", 0.65, 0.0, 1.0);
        SWEET_BAG_LOOT_AMOUNT = builder
                .comment("甜梦包开出物品的数量")
                .comment("Number of items obtained from the Sweet Bag bundle")
                .defineInRange("sweetBagLootAmount", 1, 1, Integer.MAX_VALUE);

        builder.comment("美梦包").push("Dream Bag");
        DREAM_BAG_CHANCE = builder
                .comment("获得美梦包的概率")
                .comment("Probability of receiving Dream Bag when boosted")
                .defineInRange("dreamBagChance", 0.05, 0.0, 1.0);
        DREAM_BAG_AMOUNT = builder
                .comment("获得美梦包的数量")
                .comment("Amount of Dream Bag received")
                .defineInRange("dreamBagAmount", 1, 1, Integer.MAX_VALUE);
        BOOSTED_DREAM_BAG_CHANCE = builder
                .comment("品质提升后给予美梦包的概率")
                .comment("Chance to grant a Dream Bag bundle after quality upgrade")
                .defineInRange("boostedDreamBagChance", 0.05, 0.0, 1.0);
        DREAM_BAG_LOOT_AMOUNT = builder
                .comment("美梦包开出物品的数量")
                .comment("Number of items obtained from the Dream Bag bundle")
                .defineInRange("dreamBagLootAmount", 1, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.comment("连续睡觉配置").push("Continuous sleeping configuration");
        QUALITY_BOOST_THRESHOLD = builder
                .comment("品质提升的天数阈值")
                .comment("Quality upgrade day threshold")
                .defineInRange("qualityBoostThreshold", 5, 1, Integer.MAX_VALUE);
        MAX_STREAK_THRESHOLD = builder
                .comment("必得美梦包的天数阈值")
                .comment("Guaranteed Sweet Dreams bundle day threshold")
                .defineInRange("maxStreakThreshold", 15, 0, Integer.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }
}