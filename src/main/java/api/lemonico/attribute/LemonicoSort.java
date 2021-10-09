package api.lemonico.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Objects;

public final class LemonicoSort {

    private final String property;
    private final LemonicoSort.Direction direction;

    public String toSql() {
        return this.property + " " + this.direction.getValue();
    }

    LemonicoSort(final String property, final LemonicoSort.Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    public static LemonicoSort.LemonicoSortBuilder builder() {
        return new LemonicoSort.LemonicoSortBuilder();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof LemonicoSort)) {
            return false;
        } else {
            LemonicoSort other = (LemonicoSort)o;
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
            } else return this$direction.equals(other$direction);
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

    public LemonicoSort withProperty(final String property) {
        return Objects.equals(this.property, property) ? this : new LemonicoSort(property, this.direction);
    }

    public LemonicoSort withDirection(final LemonicoSort.Direction direction) {
        return this.direction == direction ? this : new LemonicoSort(this.property, direction);
    }

    public String getProperty() {
        return this.property;
    }

    public LemonicoSort.Direction getDirection() {
        return this.direction;
    }

    public static class LemonicoSortBuilder {
        private String property;
        private LemonicoSort.Direction direction;

        LemonicoSortBuilder() {
        }

        public LemonicoSort.LemonicoSortBuilder property(final String property) {
            this.property = property;
            return this;
        }

        public LemonicoSort.LemonicoSortBuilder direction(final LemonicoSort.Direction direction) {
            this.direction = direction;
            return this;
        }

        public LemonicoSort build() {
            return new LemonicoSort(this.property, this.direction);
        }

        public String toString() {
            return "LemonicoSort.LemonicoSortBuilder(property=" + this.property + ", direction=" + this.direction + ")";
        }
    }

    public static enum Direction {
        ASC("ASC"),
        DESC("DESC");

        private final String value;

        @JsonCreator
        public static LemonicoSort.Direction of(String value) {
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