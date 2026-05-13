package me.suxuan.slimearena.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * 竞技场世界管理器 API
 * 用于管理基于模板的临时小游戏世界的生命周期。
 */
public interface ArenaManager {

	/**
	 * 异步创建一个基于模板的临时小游戏世界。
	 *
	 * @param templateName 模板世界的名称（对应 templates 文件夹下的 .slime 文件名，不带后缀）
	 * @param instanceName 将要创建的临时世界实例名称（例如 "bedwars_map1_game8"）
	 * @return CompletableFuture 包含创建成功后的 Bukkit World 对象。如果失败则抛出异常。
	 */
	@NotNull
	CompletableFuture<World> createArenaAsync(@NotNull String templateName, @NotNull String instanceName);

	/**
	 * 安全地销毁并卸载一个临时世界。
	 * 该操作不会将任何更改保存到硬盘，完全用后即焚。
	 *
	 * @param world            需要销毁的 Bukkit World 对象
	 * @param fallbackLocation 如果世界内还有玩家，强制将他们传送到的安全位置（例如主城）。若为 null，则踢出服务器。
	 * @return CompletableFuture 销毁完成后的回调
	 */
	@NotNull
	CompletableFuture<Void> discardArenaAsync(@NotNull World world, @Nullable Location fallbackLocation);

	/**
	 * 判断一个世界是否是由本管理器生成的临时 Slime 世界。
	 *
	 * @param world Bukkit World 对象
	 * @return true 如果是临时小游戏世界
	 */
	boolean isArenaWorld(@NotNull World world);
}