package mcjty.rftoolsutility.modules.teleporter.data;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class TeleportDestination {
    private final BlockPos coordinate;
    private final DimensionType dimension;
    private String name = "";

    public TeleportDestination(PacketBuffer buf) {
        int cx = buf.readInt();
        int cy = buf.readInt();
        int cz = buf.readInt();
        if (cx == -1 && cy == -1 && cz == -1) {
            coordinate = null;
        } else {
            coordinate = new BlockPos(cx, cy, cz);
        }
        dimension = DimensionType.getById(buf.readInt());
        setName(buf.readString(32767));
    }

    public TeleportDestination(BlockPos coordinate, DimensionType dimension) {
        this.coordinate = coordinate;
        this.dimension = dimension;
    }

    public boolean isValid() {
        return coordinate != null;
    }

    public void toBytes(PacketBuffer buf) {
        if (coordinate == null) {
            buf.writeInt(-1);
            buf.writeInt(-1);
            buf.writeInt(-1);
        } else {
            buf.writeInt(coordinate.getX());
            buf.writeInt(coordinate.getY());
            buf.writeInt(coordinate.getZ());
        }
        buf.writeInt(dimension.getId());
        buf.writeString(getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    public BlockPos getCoordinate() {
        return coordinate;
    }

    public DimensionType getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeleportDestination that = (TeleportDestination) o;
        return Objects.equals(coordinate, that.coordinate) &&
                Objects.equals(dimension, that.dimension) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate, dimension, name);
    }
}
