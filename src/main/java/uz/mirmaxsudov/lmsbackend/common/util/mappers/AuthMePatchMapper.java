package uz.mirmaxsudov.lmsbackend.common.util.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.request.auth.AuthMeRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface AuthMePatchMapper {
    @Mapping(target = "email", source = "email", qualifiedByName = "trim")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trim")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trim")
    @Mapping(target = "middleName", source = "middleName", qualifiedByName = "trimToNull")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "trimToNull")
    @Mapping(target = "birthDate", source = "birthDate", qualifiedByName = "toStartOfDay")
    void patch(AuthMeRequest request, @MappingTarget User user);

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }

    @Named("trimToNull")
    default String trimToNull(String value) {
        if (value == null)
            return null;

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    @Named("toStartOfDay")
    default LocalDateTime toStartOfDay(LocalDate value) {
        return value == null ? null : value.atStartOfDay();
    }
}
