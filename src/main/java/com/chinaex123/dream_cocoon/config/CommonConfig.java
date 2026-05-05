package com.chinaex123.dream_cocoon.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CommonConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue DREAM_BAG_CHANCE;
    public static final ModConfigSpec.IntValue SWEET_BAG_CHANCE;
    public static final ModConfigSpec.IntValue GOODIE_BAG_CHANCE;

    public static final ModConfigSpec.IntValue DREAM_BAG_AMOUNT;
    public static final ModConfigSpec.IntValue SWEET_BAG_AMOUNT;
    public static final ModConfigSpec.IntValue GOODIE_BAG_AMOUNT;

    public static final ModConfigSpec.IntValue BOOSTED_DREAM_BAG_CHANCE;
    public static final ModConfigSpec.IntValue BOOSTED_SWEET_BAG_CHANCE;
    public static final ModConfigSpec.IntValue BOOSTED_GOODIE_BAG_CHANCE;

    public static final ModConfigSpec.IntValue DREAM_BAG_LOOT_AMOUNT;
    public static final ModConfigSpec.IntValue SWEET_BAG_LOOT_AMOUNT;
    public static final ModConfigSpec.IntValue GOODIE_BAG_LOOT_AMOUNT;

    public static final ModConfigSpec.IntValue QUALITY_BOOST_THRESHOLD;
    public static final ModConfigSpec.IntValue MAX_STREAK_THRESHOLD;


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("CommonConfig");

        builder.comment("Probability settings for bags received after sleeping (0-100, total should be 100)")
                .push("SleepRewardProbability");
        GOODIE_BAG_CHANCE = builder.comment("Probability of receiving Goodie Bag")
                .defineInRange("goodieBagChance", 70, 0, 100);
        SWEET_BAG_CHANCE = builder.comment("Probability of receiving Sweet Bag")
                .defineInRange("sweetBagChance", 25, 0, 100);
        DREAM_BAG_CHANCE = builder.comment("Probability of receiving Dream Bag")
                .defineInRange("dreamBagChance", 5, 0, 100);
        builder.pop();

        builder.comment("Amount of bags received after sleeping")
                .push("SleepRewardAmount");
        GOODIE_BAG_AMOUNT = builder.comment("Amount of Goodie Bag received")
                .defineInRange("goodieBagAmount", 1, 1, 64);
        SWEET_BAG_AMOUNT = builder.comment("Amount of Sweet Bag received")
                .defineInRange("sweetBagAmount", 1, 1, 64);
        DREAM_BAG_AMOUNT = builder.comment("Amount of Dream Bag received")
                .defineInRange("dreamBagAmount", 1, 1, 64);
        builder.pop();

        builder.comment("Probability settings when quality is boosted")
                .push("BoostedRewardProbability");
        BOOSTED_GOODIE_BAG_CHANCE = builder.comment("Probability of receiving Goodie Bag when boosted")
                .defineInRange("boostedGoodieBagChance", 30, 0, 100);
        BOOSTED_SWEET_BAG_CHANCE = builder.comment("Probability of receiving Sweet Bag when boosted")
                .defineInRange("boostedSweetBagChance", 65, 0, 100);
        BOOSTED_DREAM_BAG_CHANCE = builder.comment("Probability of receiving Dream Bag when boosted")
                .defineInRange("boostedDreamBagChance", 5, 0, 100);
        builder.pop();

        builder.comment("Amount of items received from opening bags")
                .push("BagLootAmount");
        GOODIE_BAG_LOOT_AMOUNT = builder.comment("Amount of items received from Goodie Bag")
                .defineInRange("goodieBagLootAmount", 1, 1, 64);
        SWEET_BAG_LOOT_AMOUNT = builder.comment("Amount of items received from Sweet Bag")
                .defineInRange("sweetBagLootAmount", 1, 1, 64);
        DREAM_BAG_LOOT_AMOUNT = builder.comment("Amount of items received from Dream Bag")
                .defineInRange("dreamBagLootAmount", 1, 1, 64);
        builder.pop();

        builder.comment("Streak settings")
                .push("StreakSettings");
        QUALITY_BOOST_THRESHOLD = builder.comment("Days of consecutive sleep to trigger quality boost")
                .defineInRange("qualityBoostThreshold", 5, 1, 100);
        MAX_STREAK_THRESHOLD = builder.comment("Days of consecutive sleep to guarantee Dream Bag (0 to disable)")
                .defineInRange("maxStreakThreshold", 15, 0, 100);
        builder.pop();


        builder.pop();

        SPEC = builder.build();
    }
}
