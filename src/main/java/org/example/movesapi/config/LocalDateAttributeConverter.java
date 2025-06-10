package org.example.movesapi.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, String> {
    @Override
    public String convertToDatabaseColumn(LocalDate locDate) {
        return (locDate == null ? null : locDate.toString());
    }
    @Override
    public LocalDate convertToEntityAttribute(String sqlDate) {
        return (sqlDate == null ? null : LocalDate.parse(sqlDate));
    }
}
