package hexlet.code.mapper;

import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JsonNullableMapper nullableMapper;

    public abstract UserDTO map(User model);

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User map(UserCreateDTO dto);

    @Mapping(target = "passwordDigest", source = "password")
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    @BeforeMapping
    public final void encryptPassword(UserCreateDTO data) {
        var password = data.getPassword();
        data.setPassword(encoder.encode(password));
    }

    @BeforeMapping
    public final void encryptPassword(UserUpdateDTO data) {
        if (nullableMapper.isPresent(data.getPassword())) {
            var password = nullableMapper.unwrap(data.getPassword());
            var passwordDigest = nullableMapper.wrap(encoder.encode(password));
            data.setPassword(passwordDigest);
        }
    }
}

