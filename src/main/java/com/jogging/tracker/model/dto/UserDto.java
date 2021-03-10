package com.jogging.tracker.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.util.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.jogging.tracker.util.Markers.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;

    @NotBlank(groups = {CreateUser.class, Login.class, Register.class})
    @Email(groups = {CreateUser.class, UpdateUser.class, Register.class})
    private String email;

    @NotBlank(groups = {CreateUser.class, Register.class})
    private String firstName;

    @NotBlank(groups = {CreateUser.class, Register.class})
    private String lastName;

    @NotBlank(groups = {CreateUser.class, Login.class, Register.class})
    private String password;


    @ValueOfEnum(enumClass = User.Status.class, groups = {UpdateUser.class})
    private String status;

    @NotNull(groups = CreateUser.class)
    @ValueOfEnum(enumClass = User.Role.class, groups = {CreateUser.class, UpdateUser.class})
    private String role;

    private Integer failedLoginAttempts;


    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPassword(user.getPassword());
        dto.setStatus(user.getStatus().toString());
        dto.setRole(user.getRole().toString());
        dto.setFailedLoginAttempts(user.getFailedLoginAttempts());

        return dto;
    }
}
