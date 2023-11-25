package org.k8loud.executor.cnapp.sockshop.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserParams {
    private final String username;
    private final String password;
    private final String email;
}
