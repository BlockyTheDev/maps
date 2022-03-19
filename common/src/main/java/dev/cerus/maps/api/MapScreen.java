package dev.cerus.maps.api;

import dev.cerus.maps.api.graphics.MapScreenGraphics;
import dev.cerus.maps.api.version.VersionAdapter;
import java.util.Arrays;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MapScreen {

    private final VersionAdapter versionAdapter;
    private final ClientsideMap[][] mapArray;
    private final MapScreenGraphics graphics;
    private final int width;
    private final int height;
    private int[][] frameIds;

    public MapScreen(final VersionAdapter versionAdapter, final int w, final int h) {
        this.versionAdapter = versionAdapter;
        this.mapArray = new ClientsideMap[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                this.mapArray[x][y] = new ClientsideMap();
            }
        }
        this.graphics = new MapScreenGraphics(w, h);
        this.width = w;
        this.height = h;
    }

    public MapScreen(final VersionAdapter versionAdapter, final int w, final int h, final ClientsideMap[][] mapArray) {
        this.versionAdapter = versionAdapter;
        this.mapArray = mapArray;
        this.graphics = new MapScreenGraphics(w, h);
        this.width = w;
        this.height = h;
    }

    public void addMarker(final Marker marker) {
        final int arrX = marker.getX() / 256;
        final int arrY = marker.getY() / 256;
        System.out.println(marker.getX() + " > " + arrX + " > " + marker.getCompressedX() + ", "
                + marker.getY() + " > " + arrY + " > " + marker.getCompressedY());
        if (arrX < this.width && arrY < this.height) {
            this.mapArray[arrX][arrY].addMarker(marker);
        }
    }

    public void removeMarker(final Marker marker) {
        final int arrX = marker.getX() / 128;
        final int arrY = marker.getY() / 128;
        if (arrX < this.width && arrY < this.height) {
            this.mapArray[arrX][arrY].addMarker(marker);
        }
    }

    public void clearMarkers() {
        for (final ClientsideMap[] array : this.mapArray) {
            for (final ClientsideMap clientsideMap : array) {
                clientsideMap.clearMarkers();
            }
        }
    }

    public void sendMaps(final boolean full) {
        this.sendMaps(full, Bukkit.getOnlinePlayers());
    }

    public void sendMaps(final boolean full, final Collection<? extends Player> players) {
        this.sendMaps(full, players.toArray(Player[]::new));
    }

    public void sendMaps(final boolean full, final Player... players) {
        this.graphics.renderOnto(this, this.mapArray);

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                final ClientsideMap map = this.mapArray[x][y];
                if (!full && map.getX() == 0 && map.getY() == 0 && map.getWidth() == 0
                        && map.getHeight() == 0 && !map.hasDirtyMarkers()) {
                    continue;
                }

                final Object packet = this.versionAdapter.makeMapPacket(full, map);
                for (final Player player : players) {
                    this.versionAdapter.sendPacket(player, packet);
                }
                map.setDirtyMarkers(false);
            }
        }
    }

    public void sendFrames(final Player... players) {
        if (this.frameIds == null) {
            return;
        }

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                final ClientsideMap map = this.mapArray[x][y];
                final int frameId = this.frameIds[x][y];

                for (final Player player : players) {
                    this.versionAdapter.sendPacket(player, this.versionAdapter.makeFramePacket(frameId, map));
                }
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public MapScreenGraphics getGraphics() {
        return this.graphics;
    }

    public int[][] getFrameIds() {
        return this.frameIds;
    }

    public void setFrameIds(final int[][] frameIds) {
        this.frameIds = frameIds;
    }

    public Collection<Marker> getMarkers() {
        return Arrays.stream(this.mapArray)
                .flatMap(Arrays::stream)
                .flatMap(clientsideMap -> clientsideMap.getMarkers().stream())
                .toList();
    }

}
