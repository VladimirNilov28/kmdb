package org.example.movesapi.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

/**
 * JPA AttributeConverter for converting {@link LocalDate} to {@link String} and vice versa.
 * <p>
 * This allows storing LocalDate fields as Strings in the database.
 * Automatically applied to all LocalDate fields due to {@code autoApply = true}.
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, String> {

    /**
     * Converts a LocalDate value to a String to be stored in the database.
     *
     * @param locDate the LocalDate value from the entity
     * @return the String representation (e.g. "2024-12-01") or null if input is null
     */
    @Override
    public String convertToDatabaseColumn(LocalDate locDate) {
        return (locDate == null ? null : locDate.toString());
    }

    /**
     * Converts a String value from the database into a LocalDate.
     *
     * @param sqlDate the date string from the database (e.g. "2024-12-01")
     * @return the corresponding LocalDate or null if input is null
     */
    @Override
    public LocalDate convertToEntityAttribute(String sqlDate) {
        return (sqlDate == null ? null : LocalDate.parse(sqlDate));
    }
}

/*
    Why is this needed?

    Some databases (like SQLite) do not natively support the Java 8+ java.time types (e.g., LocalDate).
    This converter tells JPA/Hibernate how to store and retrieve LocalDate fields:
    - Store them as ISO-8601 formatted strings (e.g. "2024-12-01")
    - Parse them back into LocalDate objects on load

    With @Converter(autoApply = true), this converter is automatically used for all LocalDate fields
    across your entities, without needing to add @Convert on every field manually.
*/
