package dev.cerus.maps.api.version;

import dev.cerus.maps.api.ClientsideMap;
import dev.cerus.maps.api.Frame;
import org.bukkit.entity.Player;

/**
 * Interface for version specific code
 */
public interface VersionAdapter {

    /**
     * Make a packet to send a map to a player
     *
     * @param ignoreBounds If true, implementations should use the full buffer and ignore the bounds
     * @param map          The map
     *
     * @return The new packet
     */
    Object makeMapPacket(boolean ignoreBounds, ClientsideMap map);

    /**
     * Make a frame update packet to display fake items
     *
     * @param frameId The id of the frame
     * @param map     The map to display
     *
     * @return The new packet
     */
    Object makeFramePacket(int frameId, boolean visible, ClientsideMap map);

    default Object makeFramePacket(final int frameId, final ClientsideMap map) {
        return this.makeFramePacket(frameId, true, map);
    }

    /**
     * Make a frame spawn packet
     *
     * @param frame The frame to spawn
     *
     * @return The spawn packet
     */
    Object makeFrameSpawnPacket(Frame frame);

    /**
     * Make a frame despawn packet
     *
     * @param frame The frame to despawn
     *
     * @return The despawn packet
     */
    Object makeFrameDespawnPacket(Frame frame);

    /**
     * Send a packet to a playe
     *
     * @param player The player
     * @param packet The packet
     */
    void sendPacket(Player player, Object packet);

}
