package com.chinaex123.dream_cocoon.event.reward;

import com.chinaex123.dream_cocoon.config.CommonConfig;
import com.chinaex123.dream_cocoon.init.ModItems;
import net.minecraft.world.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 计算玩家睡觉后获得的奖励包裹
 * <p>
 * 功能：
 * - 根据连续睡觉天数计算奖励类型和数量
 * - 支持普通奖励和品质提升后的奖励
 * - 当连续天数达到阈值时必得美梦包
 * - 使用配置的概率值随机决定包裹类型
 */
public class RewardCalculator {

    public record RewardData(Item item, int amount) {}

    /**
     * 根据玩家当前的连续睡觉天数计算应发放的奖励
     * <p>
     * 功能：
     * - 检查是否达到最大连续天数阈值，若达到则直接发放美梦包
     * - 判断是否达到品质提升阈值，决定后续随机奖励的品质等级
     * - 调用随机奖励计算逻辑生成最终奖励内容
     *
     * @param currentStreak 玩家当前的连续睡觉天数
     * @return 包含奖励物品和数量的 RewardData 对象
     */
    public static RewardData calculateReward(int currentStreak) {
        // 获取配置的最大连续天数阈值
        int maxStreak = CommonConfig.MAX_STREAK_THRESHOLD.get();

        // 如果达到最大阈值，直接返回最高品质奖励（美梦包）
        if (maxStreak > 0 && currentStreak >= maxStreak) {
            return new RewardData(ModItems.DREAM_BAG.get(), CommonConfig.DREAM_BAG_AMOUNT.get());
        }

        // 获取品质提升所需的连续天数阈值
        int qualityBoostThreshold = CommonConfig.QUALITY_BOOST_THRESHOLD.get();
        // 判断当前连续天数是否已触发品质提升
        boolean isQualityBoosted = currentStreak >= qualityBoostThreshold;

        // 根据品质状态计算并返回随机奖励
        return calculateRandomReward(isQualityBoosted);
    }

    /**
     * 根据品质状态计算随机奖励内容
     * <p>
     * 功能：
     * - 生成 0-99 的随机数作为概率判定依据
     * - 根据是否触发品质提升，分发到不同的奖励计算逻辑
     * - 决定玩家获得的是普通奖励还是高品质奖励
     *
     * @param qualityBoosted 是否已触发品质提升（连续天数达到阈值）
     * @return 计算后的随机奖励数据对象
     */
    private static RewardData calculateRandomReward(boolean qualityBoosted) {
        // 生成 0 到 99 之间的随机整数用于概率判定
        int chance = ThreadLocalRandom.current().nextInt(100);

        // 根据品质状态选择对应的奖励计算分支
        if (qualityBoosted) {
            return calculateBoostedReward(chance);
        } else {
            return calculateNormalReward(chance);
        }
    }

    /**
     * 计算品质提升状态下的奖励内容（双倍数量）
     * <p>
     * 功能：
     * - 根据配置的掉落概率判定发放美梦包、甜梦包或好梦包
     * - 所有奖励物品的数量均为基础配置的两倍
     * - 使用累加概率区间进行随机判定
     *
     * @param chance 0-99 的随机数，用于概率判定
     * @return 包含高品质奖励物品和双倍数量的 RewardData 对象
     */
    private static RewardData calculateBoostedReward(int chance) {
        // 获取配置的美梦包掉落概率
        int dreamChance = CommonConfig.BOOSTED_DREAM_BAG_CHANCE.get();
        // 获取配置的甜梦包掉落概率
        int sweetChance = CommonConfig.BOOSTED_SWEET_BAG_CHANCE.get();

        // 如果随机数落在美梦包区间，返回双倍美梦包
        if (chance < dreamChance) {
            return new RewardData(ModItems.DREAM_BAG.get(), CommonConfig.DREAM_BAG_AMOUNT.get() * 2);
        } else if (chance < dreamChance + sweetChance) {
            // 如果随机数落在甜梦包区间，返回双倍甜梦包
            return new RewardData(ModItems.SWEET_BAG.get(), CommonConfig.SWEET_BAG_AMOUNT.get() * 2);
        } else {
            // 否则返回双倍好梦包
            return new RewardData(ModItems.GOODIE_BAG.get(), CommonConfig.GOODIE_BAG_AMOUNT.get() * 2);
        }
    }

    /**
     * 计算普通状态下的奖励内容（基础数量）
     * <p>
     * 功能：
     * - 根据配置的掉落概率判定发放美梦包、甜梦包或好梦包
     * - 所有奖励物品的数量为基础配置值
     * - 使用累加概率区间进行随机判定
     *
     * @param chance 0-99 的随机数，用于概率判定
     * @return 包含基础奖励物品和数量的 RewardData 对象
     */
    private static RewardData calculateNormalReward(int chance) {
        // 获取配置的美梦包掉落概率
        int dreamChance = CommonConfig.DREAM_BAG_CHANCE.get();
        // 获取配置的甜梦包掉落概率
        int sweetChance = CommonConfig.SWEET_BAG_CHANCE.get();

        // 如果随机数落在美梦包区间，返回基础数量的美梦包
        if (chance < dreamChance) {
            return new RewardData(ModItems.DREAM_BAG.get(), CommonConfig.DREAM_BAG_AMOUNT.get());
        } else if (chance < dreamChance + sweetChance) {
            // 如果随机数落在甜梦包区间，返回基础数量的甜梦包
            return new RewardData(ModItems.SWEET_BAG.get(), CommonConfig.SWEET_BAG_AMOUNT.get());
        } else {
            // 否则返回基础数量的好梦包
            return new RewardData(ModItems.GOODIE_BAG.get(), CommonConfig.GOODIE_BAG_AMOUNT.get());
        }
    }
}
