package pacifist.teach;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class PacifistTeachPlugin extends JavaPlugin implements Listener {

    List<Player> gliding = new ArrayList<>();

    List<Player> cooltime = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent e) {

        Player player = e.getPlayer();

        if (!player.isOnGround()) {
            return;
        }

        if (isCooldown(player)) {
            if (e.isSneaking()) {
                player.sendMessage("§7クールダウン中です");
            }
            return;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 1);
        player.setGliding(true);
        gliding.add(player);
        //クールタイムリストに突っ込む
        setCooltime(player);


        new BukkitRunnable() {
            @Override
            public void run() {
                if (gliding.contains(player)) {
                    gliding.remove(player);
                }
                this.cancel();
            }
        }.runTaskTimer(this, 60, 0);
    }

    @EventHandler
    private void onMove(PlayerMoveEvent e) {
        if (gliding.contains(e.getPlayer())) {
            Player player = e.getPlayer();

            if (!player.isOnGround()) {
                gliding.remove(player);

                //クールタイムリストに突っ込む
                setCooltime(player);

                player.setGliding(false);
            }
        }
    }

    @EventHandler
    private void onGlide(EntityToggleGlideEvent e) {

        if (e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) e.getEntity();

        if (gliding.contains(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp()) {
            e.setJoinMessage("§4§lOperator §f§l" + e.getPlayer().getName() + "さんがサーバーに入りました！");
            return;
        }
        e.setJoinMessage(e.getPlayer().getName() + "さんがサーバーに入りました！");
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(e.getPlayer().getName() + "さんがサーバーに入りました！");
    }

    private void setCooltime(Player player) {
        //クールタイムリストに追加
        cooltime.add(player);

        //4.5秒後にクールタイムリストから外す
        new BukkitRunnable() {
            @Override
            public void run() {
                cooltime.remove(player);
                this.cancel();
            }
            //delay
        }.runTaskTimer(this, 90, 0);
    }

    private boolean isCooldown(Player player) {
        if (cooltime.contains(player)) {
            return true;
        }

        return false;
    }
}
