package api.lemonico.core.attribute;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Objects;

public final class LcSort
{

    private final String property;
    private final Direction direction;

    public String toSql() {
        return this.property + " " + this.direction.getValue();
    }

    public LcSort(final String property, final Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    public static LcSort.LemonicoSortBuilder builder() {
        return new LcSort.LemonicoSortBuilder();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof LcSort)) {
            return false;
        } else {
            LcSort other = (LcSort) o;
            Object this$property = this.getProperty();
            Object other$property = other.getProperty();
            if (this$property == null) {
                if (other$property != null) {
                    return false;
                }
            } else if (!this$property.equals(other$property)) {
                return false;
            }

            Object this$direction = this.getDirection();
            Object other$direction = other.getDirection();
            if (this$direction == null) {
                return other$direction == null;
            } else
                return this$direction.equals(other$direction);
        }
    }

    public int hashCode() {
        Object $property = this.getProperty();
        int result = 59 + ($property == null ? 43 : $property.hashCode());
        Object $direction = this.getDirection();
        result = result * 59 + ($direction == null ? 43 : $direction.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getProperty();
        return "LemonicoSort(property=" + var10000 + ", direction=" + this.getDirection() + ")";
    }

    public LcSort withProperty(final String property) {
        return Objects.equals(this.property, property) ? this : new LcSort(property, this.direction);
    }

    public LcSort withDirection(final LcSort.Direction direction) {
        return this.direction == direction ? this : new LcSort(this.property, direction);
    }

    public String getProperty() {
        return this.property;
    }

    public LcSort.Direction getDirection() {
        return this.direction;
    }

    public static class LemonicoSortBuilder
    {
        private String property;
        private LcSort.Direction direction;

        LemonicoSortBuilder() {}

        public LcSort.LemonicoSortBuilder property(final String property) {
            this.property = property;
            return this;
        }

        public LcSort.LemonicoSortBuilder direction(final Direction direction) {
            this.direction = direction;
            return this;
        }

        public LcSort build() {
            return new LcSort(this.property, this.direction);
        }

        public String toString() {
            return "LemonicoSort.LemonicoSortBuilder(property=" + this.property + ", direction=" + this.direction + ")";
        }
    }

    public static enum Direction
    {
        ASC("ASC"), DESC("DESC");

        private final String value;

        @JsonCreator
        public static LcSort.Direction of(String value) {
            return Arrays.stream(values())
                .filter((v) -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Direction = '" + value + "' is not supported."));
        }

        Direction(final String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return this.value;
        }
    }
}
