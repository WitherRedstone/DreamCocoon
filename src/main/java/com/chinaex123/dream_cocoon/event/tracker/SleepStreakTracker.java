package com.chinaex123.dream_cocoon.event.tracker;

import com.chinaex123.dream_cocoon.data.SleepStreakSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * 管理玩家连续睡觉天数的追踪器
 * <p>
 * 功能：
 * - 记录和获取玩家的连续睡觉天数
 * - 在每天开始时检查并重置中断的连续睡觉记录
 * - 当连续睡觉中断时向玩家发送提示消息
 */
public class SleepStreakTracker {

    /**
     * 获取指定玩家的当前连续睡觉天数
     * <p>
     * 功能：
     * - 从世界存档的持久化数据中读取玩家的连续睡觉记录
     * - 仅适用于服务端玩家实体
     *
     * @param player 要查询的玩家对象
     * @return 该玩家的连续睡觉天数
     */
    public static int getStreak(Player player) {
        // 将玩家所在的世界层级转换为服务端层级
        ServerLevel level = (ServerLevel) player.level();
        // 获取该世界的连续睡觉数据存储对象
        SleepStreakSavedData data = SleepStreakSavedData.get(level);
        // 返回指定玩家的连续睡觉天数
        return data.getStreak(player.getUUID());
    }

    /**
     * 记录玩家的睡觉行为，更新连续睡觉天数
     * <p>
     * 功能：
     * - 计算当前游戏世界天数
     * - 将本次睡觉记录保存到持久化数据中
     * - 自动处理连续天数的累加逻辑
     *
     * @param player 执行睡觉行为的玩家对象
     */
    public static void recordSleep(Player player) {
        // 获取玩家所在的服务端世界层级
        ServerLevel level = (ServerLevel) player.level();
        // 获取连续睡觉数据存储对象
        SleepStreakSavedData data = SleepStreakSavedData.get(level);
        // 计算当前世界天数（游戏刻 / 24000）
        long currentDay = player.level().getDayTime() / 24000;

        // 记录玩家在当前天数的睡觉行为
        data.recordSleep(player.getUUID(), currentDay);
    }

    /**
     * 重置指定玩家的连续睡觉天数
     * <p>
     * 功能：
     * - 将玩家的连续睡觉记录清零
     * - 通常用于检测到玩家中断睡觉或手动修改时间时
     *
     * @param player 需要重置数据的玩家对象
     */
    public static void resetStreak(Player player) {
        // 获取玩家所在的服务端世界层级
        ServerLevel level = (ServerLevel) player.level();
        // 获取连续睡觉数据存储对象
        SleepStreakSavedData data = SleepStreakSavedData.get(level);
        // 执行重置操作，清除该玩家的连续天数记录
        data.resetStreak(player.getUUID());
    }

    /**
     * 检查并重置所有中断连续睡觉的玩家记录
     * <p>
     * 功能：
     * - 仅在服务端每天的起始时刻（游戏刻为 0）执行检查
     * - 遍历世界中的所有玩家，对比上次睡觉日期与当前日期
     * - 如果间隔超过一天，则将该玩家的连续天数清零
     *
     * @param event 世界刻后置事件对象，用于获取当前世界层级和执行时机
     */
    public static void checkAndResetStreaks(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        // 获取服务端世界层级对象
        ServerLevel level = (ServerLevel) event.getLevel();
        // 计算当前在一天中的具体时刻（0-23999）
        long dayTime = level.getDayTime() % 24000;

        // 仅在世界天的起始时刻（第 0 刻）执行检查逻辑
        if (dayTime != 0) {
            return;
        }

        // 获取连续睡觉数据存储对象
        SleepStreakSavedData data = SleepStreakSavedData.get(level);
        // 计算当前的世界总天数
        long currentDay = level.getDayTime() / 24000;

        // 遍历世界中的所有在线玩家进行检查
        level.players().forEach(player -> {
            // 获取该玩家的睡觉数据记录
            SleepStreakSavedData.PlayerSleepData playerData = data.getPlayerData(player.getUUID());
            // 获取玩家上一次睡觉所在的世界天数
            long lastSleepDay = playerData.getLastSleepDay();

            // 如果玩家有过睡觉记录且距离上次睡觉已超过一天，则重置连续天数
            if (lastSleepDay != -1 && currentDay - lastSleepDay > 1) {
                playerData.setConsecutiveDays(0);
            }
        });
    }
}
