package api.lemonico.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.io.Serializable;
import java.util.Objects;
import org.seasar.doma.Domain;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Domain(
        valueType = Long.class,
        factoryMethod = "of"
)
public final class ID<E> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long value;

    private ID(final Long value) {
        this.value = value;
    }

    public Long getValue() {
        return this.value;
    }

    @JsonValue
    public String toString() {
        return String.valueOf(this.value);
    }

    public static <R> ID<R> of(final Long value) {
        try {
            Objects.requireNonNull(value);
            if (value <= 0L) {
                throw new IllegalArgumentException("The identifier must be greater than 0.");
            }
        } catch (NullPointerException var2) {
            throw new IllegalArgumentException(var2);
        }

        return new ID(value);
    }

    @JsonCreator(
            mode = Mode.DELEGATING
    )
    public static <R> ID<R> of(final String value) {
        try {
            return of(Long.parseLong(value));
        } catch (NumberFormatException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ID<?> id = (ID)o;
            return this.value.equals(id.value);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Component
    public static class IDConverter implements Converter<String, ID<?>> {
        public IDConverter() {
        }

        public ID<?> convert(String value) {
            return ID.of(value);
        }
    }
}