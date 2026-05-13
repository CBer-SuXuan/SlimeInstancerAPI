package me.suxuan.slimearena;

import me.suxuan.slimearena.api.ArenaManager;
import me.suxuan.slimearena.impl.ASPArenaManagerImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SlimeArenaAPI 插件主类
 * 负责插件的生命周期管理，如加载配置、检测核心环境和暴露服务接口。
 */
public final class SlimeArenaPlugin extends JavaPlugin {

	private static SlimeArenaPlugin instance;
	private final MiniMessage mm = MiniMessage.miniMessage();

	// 保存管理器的引用，用于在卸载时调用清理
	private ASPArenaManagerImpl arenaManagerImpl;

	@Override
	public void onEnable() {
		instance = this;

		if (!checkSlimePaperEnvironment()) {
			log("<red>启动失败！未检测到 AdvancedSlimePaper 核心。</red>");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// 实例化管理器
		this.arenaManagerImpl = new ASPArenaManagerImpl(this);

		// 核心步骤：将 API 注册到 Bukkit 的服务管理器中
		// 这样其他插件就可以通过 Bukkit.getServicesManager().load(ArenaManager.class) 来调用了
		getServer().getServicesManager().register(
				ArenaManager.class,
				this.arenaManagerImpl,
				this,
				ServicePriority.Normal
		);

		log("<green>SlimeArenaAPI 已成功启用并完成服务注册！</green>");
	}

	@Override
	public void onDisable() {
		if (this.arenaManagerImpl != null) {
			log("<yellow>正在执行安全清理，卸载残留的临时世界...</yellow>");

			// 遍历所有尚未销毁的世界，强制执行卸载
			for (String arenaName : this.arenaManagerImpl.getActiveArenas()) {
				World world = Bukkit.getWorld(arenaName);
				if (world != null) {
					// 注意：在 onDisable 中必须使用同步操作，因此不调用 discardArenaAsync
					// 这里直接进行无保存的强制卸载
					for (org.bukkit.entity.Player player : world.getPlayers()) {
						player.kick(Component.text("服务器正在关闭/重启，小游戏世界已强制解散。"));
					}
					Bukkit.unloadWorld(world, false);
					log("<gray>强制清理残留世界: <aqua>" + arenaName + "</aqua></gray>");
				}
			}
		}
		log("<yellow>SlimeArenaAPI 卸载完毕。</yellow>");
	}

	public static SlimeArenaPlugin getInstance() {
		return instance;
	}

	public void log(String miniMessageString) {
		Component message = mm.deserialize("<dark_gray>[<aqua>SlimeArenaAPI</aqua>] </dark_gray>" + miniMessageString);
		getServer().getConsoleSender().sendMessage(message);
	}

	private boolean checkSlimePaperEnvironment() {
		try {
			Class.forName("com.infernalsuite.asp.api.AdvancedSlimePaperAPI");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}