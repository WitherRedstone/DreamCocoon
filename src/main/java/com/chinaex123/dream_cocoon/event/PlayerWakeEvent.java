package com.chinaex123.dream_cocoon.event;

import com.chinaex123.dream_cocoon.DreamCocoon;
import com.chinaex123.dream_cocoon.config.CommonConfig;
import com.chinaex123.dream_cocoon.data.SleepStreakSavedData;
import com.chinaex123.dream_cocoon.event.reward.RewardCalculator;
import com.chinaex123.dream_cocoon.event.tracker.SleepStreakTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * 主事件处理器，协调各个模块
 */
@EventBusSubscriber(modid = DreamCocoon.MOD_ID)
public class PlayerWakeEvent {

    /**
     * 处理玩家醒来事件，发放睡觉奖励并更新连续睡觉天数
     * <p>
     * 功能：
     * - 记录玩家的睡觉行为，更新连续睡觉天数
     * - 根据连续天数计算并发放对应奖励物品
     * - 如果背包已满，将奖励掉落在地上
     * - 向玩家发送连续睡觉天数的提示消息
     *
     * @param event 玩家醒来事件对象
     */
    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        // 判断是否是完整睡眠
        if (!player.isSleepingLongEnough()) {
            return;
        }

        // 记录玩家本次睡觉行为，更新连续睡觉数据
        SleepStreakTracker.recordSleep(player);

        // 获取玩家当前的连续睡觉天数
        int currentStreak = SleepStreakTracker.getStreak(player);

        // 根据连续天数计算奖励内容和数量
        RewardCalculator.RewardData reward = RewardCalculator.calculateReward(currentStreak);
        ItemStack rewardStack = new ItemStack(reward.item(), reward.amount());

        // 尝试将奖励添加到玩家背包，如果失败则掉落在地上
        if (!player.getInventory().add(rewardStack)) {
            player.drop(rewardStack, false);
        }

        // 向玩家发送连续睡觉天数的提示消息
        sendStreakMessage(player, currentStreak);
    }

    /**
     * 监听世界刻事件，定期检查并重置玩家的连续睡觉天数
     * <p>
     * 功能：
     * - 在每个游戏刻结束时触发检查
     * - 检测玩家是否超过一天未睡觉
     * - 自动重置中断的连续睡觉记录并发送提示
     *
     * @param event 世界刻后置事件对象
     */
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        SleepStreakTracker.checkAndResetStreaks(event);
    }

    /**
     * 监听命令执行事件，检测时间修改命令并重置所有玩家的连续睡觉数据
     * <p>
     * 功能：
     * - 捕获包含 "day set" 或 "time set" 的命令
     * - 当管理员手动修改游戏时间时，自动重置所有玩家的连续睡觉记录
     * - 防止通过命令跳过天数来刷取连续睡觉奖励
     *
     * @param event 命令执行事件对象
     */
    @SubscribeEvent
    public static void onCommandExecuted(CommandEvent event) {
        // 获取执行的完整命令字符串
        String command = event.getParseResults().getReader().getString();

        // 检测是否为修改时间的命令（/day set 或 /time set）
        if (command.contains("day set") || command.contains("time set")) {
            // 获取当前世界层级对象
            ServerLevel level = event.getParseResults().getContext().getSource().getLevel();
            // 获取连续睡觉数据存储对象
            SleepStreakSavedData data = SleepStreakSavedData.get(level);

            // 遍历世界中的所有玩家
            level.players().forEach(player -> {
                // 获取当前玩家的睡觉数据
                SleepStreakSavedData.PlayerSleepData playerData = data.getPlayerData(player.getUUID());
                // 重置该玩家的连续睡觉记录
                playerData.reset();
            });

            // 标记数据已修改，需要保存到磁盘
            data.setDirty();
        }
    }

    /**
     * 向玩家发送连续睡觉天数的提示消息
     * <p>
     * 功能：
     * - 根据连续天数判断是否达到品质提升阈值
     * - 使用不同的本地化消息键显示普通或加成状态
     * - 在ActionBar位置显示消息（不占用聊天栏）
     *
     * @param player 接收消息的玩家对象
     * @param currentStreak 当前连续睡觉天数
     */
    private static void sendStreakMessage(Player player, int currentStreak) {
        // 获取配置的品质提升阈值
        int qualityBoostThreshold = CommonConfig.QUALITY_BOOST_THRESHOLD.get();
        // 判断当前连续天数是否达到品质提升条件
        boolean isQualityBoosted = currentStreak >= qualityBoostThreshold;

        Component message = isQualityBoosted ?
                Component.translatable("dream_cocoon.streak.boosted", currentStreak) :
                Component.translatable("dream_cocoon.streak", currentStreak);

        player.displayClientMessage(message, true);
    }
}
