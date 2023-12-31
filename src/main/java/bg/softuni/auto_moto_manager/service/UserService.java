package bg.softuni.auto_moto_manager.service;

import bg.softuni.auto_moto_manager.model.dto.binding.UserRegisterDTO;
import bg.softuni.auto_moto_manager.model.dto.binding.UserEditDTO;

import java.util.Optional;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);
    Optional<UserEditDTO> getUserForEdit(String email);
    void editRoles(UserEditDTO userEditDTO);
}
