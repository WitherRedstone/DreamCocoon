package com.chinaex123.dream_cocoon.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家连续睡觉数据的持久化存储
 * <p>
 * 功能：
 * - 保存每个玩家的连续睡觉天数和最后睡觉日期
 * - 将数据存档到世界存档文件中，重启游戏后不丢失
 * - 提供读取、更新和重置玩家睡觉记录的方法
 */
public class SleepStreakSavedData extends SavedData {
    private static final String DATA_NAME = "dream_cocoon_sleep_streak";

    private final Map<UUID, PlayerSleepData> playerData = new HashMap<>();

    /**
     * 玩家睡觉数据内部类，用于存储单个玩家的连续睡觉信息
     */
    public static class PlayerSleepData {
        // 连续睡觉天数，初始为 0
        private int consecutiveDays = 0;
        // 上一次睡觉所在的世界天数，-1 表示从未睡过觉
        private long lastSleepDay = -1;

        /**
         * 获取当前连续睡觉天数
         *
         * @return 连续睡觉天数
         */
        public int getConsecutiveDays() {
            return consecutiveDays;
        }

        /**
         * 设置连续睡觉天数并标记数据需要保存
         *
         * @param days 要设置的连续天数
         */
        public void setConsecutiveDays(int days) {
            this.consecutiveDays = days;
            // 标记数据已修改，需要同步到客户端并保存
            setDirty();
        }

        /**
         * 获取上一次睡觉的世界天数
         *
         * @return 上次睡觉的天数，-1 表示未记录
         */
        public long getLastSleepDay() {
            return lastSleepDay;
        }

        /**
         * 设置上一次睡觉的世界天数并标记数据需要保存
         *
         * @param day 睡觉发生时的世界天数
         */
        public void setLastSleepDay(long day) {
            this.lastSleepDay = day;
            // 标记数据已修改，需要同步到客户端并保存
            setDirty();
        }

        /**
         * 增加连续睡觉天数，自动处理中断逻辑
         * <p>
         * 如果距离上次睡觉超过一天，则先重置连续天数再累加
         *
         * @param currentDay 当前的世界天数
         */
        public void incrementStreak(long currentDay) {
            // 检测是否中断：如果上次有记录且间隔超过一天，则清零
            if (lastSleepDay != -1 && currentDay - lastSleepDay > 1) {
                consecutiveDays = 0;
            }
            // 连续天数加一
            consecutiveDays++;
            // 更新最后睡觉日期为当前天
            lastSleepDay = currentDay;
            // 标记数据已修改，需要同步到客户端并保存
            setDirty();
        }

        /**
         * 重置玩家的睡觉记录，清除连续天数和最后睡觉日期
         */
        public void reset() {
            // 清零连续天数
            consecutiveDays = 0;
            // 重置最后睡觉日期为初始状态
            lastSleepDay = -1;
            // 标记数据已修改，需要同步到客户端并保存
            setDirty();
        }

        /**
         * 标记数据为脏数据，触发服务端向客户端同步并保存到磁盘
         * <p>
         * 注意：此方法由 SavedData 框架自动实现，此处为空占位
         */
        private void setDirty() {
        }
    }

    /**
     * 从 NBT 标签中加载连续睡觉数据
     * <p>
     * 功能：
     * - 解析存档文件中保存的玩家睡觉记录
     * - 遍历所有玩家数据，重建内存中的状态对象
     * - 用于世界加载时恢复之前的连续睡觉进度
     *
     * @param tag 包含持久化数据的 NBT 复合标签
     * @param provider 注册表查找器提供者（当前未使用）
     * @return 加载完成的 SleepStreakSavedData 实例
     */
    public static SleepStreakSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        // 创建新的数据存储对象
        SleepStreakSavedData data = new SleepStreakSavedData();

        // 检查是否存在玩家数据标签
        if (tag.contains("players")) {
            // 获取玩家数据复合标签
            CompoundTag playersTag = tag.getCompound("players");
            // 遍历所有玩家的 UUID 键值
            for (String key : playersTag.getAllKeys()) {
                // 将字符串键解析为 UUID 对象
                UUID playerId = UUID.fromString(key);
                // 获取该玩家的详细数据标签
                CompoundTag playerTag = playersTag.getCompound(key);

                // 创建玩家睡觉数据对象
                PlayerSleepData playerData = new PlayerSleepData();
                // 从 NBT 中读取连续睡觉天数
                playerData.consecutiveDays = playerTag.getInt("consecutive_days");
                // 从 NBT 中读取上次睡觉的世界天数
                playerData.lastSleepDay = playerTag.getLong("last_sleep_day");

                // 将解析后的数据存入映射表
                data.playerData.put(playerId, playerData);
            }
        }

        return data;
    }

    /**
     * 将连续睡觉数据保存到 NBT 标签中
     * <p>
     * 功能：
     * - 遍历内存中所有玩家的睡觉记录
     * - 将连续天数和最后睡觉日期序列化为 NBT 格式
     * - 用于世界存档时持久化保存进度
     *
     * @param tag 用于存储数据的 NBT 复合标签
     * @param provider 注册表查找器提供者（当前未使用）
     * @return 包含所有玩家睡觉数据的 NBT 标签
     */
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        // 创建用于存储所有玩家数据的复合标签
        CompoundTag playersTag = new CompoundTag();

        // 遍历内存中的玩家数据映射表
        for (Map.Entry<UUID, PlayerSleepData> entry : playerData.entrySet()) {
            // 为每个玩家创建独立的 NBT 标签
            CompoundTag playerTag = new CompoundTag();
            // 写入连续睡觉天数
            playerTag.putInt("consecutive_days", entry.getValue().consecutiveDays);
            // 写入上次睡觉的世界天数
            playerTag.putLong("last_sleep_day", entry.getValue().lastSleepDay);
            // 以 UUID 字符串为键存入玩家标签
            playersTag.put(entry.getKey().toString(), playerTag);
        }

        // 将所有玩家数据挂载到主标签下
        tag.put("players", playersTag);
        return tag;
    }

    /**
     * 获取当前世界的连续睡觉数据存储实例
     * <p>
     * 功能：
     * - 通过 NeoForge 的 SavedData 系统管理数据生命周期
     * - 如果数据不存在则自动创建新实例，否则从存档加载
     * - 确保所有逻辑访问的是同一份持久化数据
     *
     * @param level 服务端世界层级对象，用于获取数据存储管理器
     * @return 唯一的 SleepStreakSavedData 实例
     */
    public static SleepStreakSavedData get(ServerLevel level) {
        // 从主世界的数据存储器中获取或计算实例
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                // 定义数据的工厂方法：包含构造函数和加载逻辑
                new SavedData.Factory<>(SleepStreakSavedData::new, SleepStreakSavedData::load),
                // 使用预定义的数据名称作为存储键
                DATA_NAME
        );
    }

    /**
     * 获取指定玩家的睡觉数据，如果不存在则自动创建
     *
     * @param playerId 玩家的 UUID
     * @return 该玩家的 PlayerSleepData 实例
     */
    public PlayerSleepData getPlayerData(UUID playerId) {
        // 尝试从映射表获取数据，若为空则通过 lambda 表达式创建新实例并存入
        return playerData.computeIfAbsent(playerId, k -> new PlayerSleepData());
    }

    /**
     * 获取指定玩家的当前连续睡觉天数
     *
     * @param playerId 玩家的 UUID
     * @return 连续睡觉天数，若玩家无记录则返回 0
     */
    public int getStreak(UUID playerId) {
        // 从映射表中获取玩家数据对象
        PlayerSleepData data = playerData.get(playerId);
        // 如果数据存在则返回天数，否则返回默认值 0
        return data != null ? data.getConsecutiveDays() : 0;
    }

    /**
     * 记录玩家在当前天数的睡觉行为并更新连续进度
     *
     * @param playerId 玩家的 UUID
     * @param currentDay 当前的世界天数
     */
    public void recordSleep(UUID playerId, long currentDay) {
        // 获取或创建该玩家的睡觉数据对象
        PlayerSleepData data = getPlayerData(playerId);
        // 执行连续天数的累加逻辑（含中断检测）
        data.incrementStreak(currentDay);
        // 标记整个数据存储为脏数据，触发保存和同步
        setDirty();
    }

    /**
     * 重置指定玩家的连续睡觉记录
     *
     * @param playerId 玩家的 UUID
     */
    public void resetStreak(UUID playerId) {
        // 获取该玩家的睡觉数据对象
        PlayerSleepData data = getPlayerData(playerId);
        // 执行重置操作，清零天数和日期
        data.reset();
        // 标记整个数据存储为脏数据，触发保存和同步
        setDirty();
    }

    /**
     * 重写父类方法，标记数据已修改需要同步到客户端并保存到磁盘
     */
    @Override
    public void setDirty() {
        // 调用 SavedData 框架提供的底层同步逻辑
        super.setDirty();
    }
}
