package bg.softuni.auto_moto_manager.service.impl;

import bg.softuni.auto_moto_manager.model.dto.binding.UserRegisterDTO;
import bg.softuni.auto_moto_manager.model.dto.binding.UserEditDTO;
import bg.softuni.auto_moto_manager.model.entity.RoleEntity;
import bg.softuni.auto_moto_manager.model.entity.UserEntity;
import bg.softuni.auto_moto_manager.model.enums.UserRoleEnum;
import bg.softuni.auto_moto_manager.repository.RoleRepository;
import bg.softuni.auto_moto_manager.repository.UserRepository;
import bg.softuni.auto_moto_manager.repository.VehicleRepository;
import bg.softuni.auto_moto_manager.service.UserService;
import bg.softuni.auto_moto_manager.service.exceptions.DatabaseException;
import bg.softuni.auto_moto_manager.service.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VehicleRepository vehicleRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           VehicleRepository vehicleRepository,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.vehicleRepository = vehicleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        UserEntity user = modelMapper.map(userRegisterDTO, UserEntity.class);
        userRepository.save(user);
    }

    @Override
    public Optional<UserEditDTO> getUserForEdit(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToUserEditDTO);
    }

    @Override
    @Transactional
    public void editRoles(UserEditDTO userEditDTO) {
        UserEntity userEntity = userRepository.findByEmail(userEditDTO.getEmail())
                .orElseThrow(() -> new ObjectNotFoundException("User with email " + userEditDTO.getEmail() + " was not found!"));

        RoleEntity adminRole = roleRepository
                .findByRole(UserRoleEnum.ADMIN)
                .orElseThrow(() -> new DatabaseException("User role 'ADMIN' was not found in database!"));

        if (userEditDTO.isAdmin()) {
            userEntity.addRole(adminRole);
        } else {
            userEntity.removeRole(adminRole);
        }

        userRepository.save(userEntity);
    }

//    @Override
//    public boolean hasPermissionToModify(String vehicleUuid) {
//        if (!vehicleRepository.existsByUuid(vehicleUuid)) {
//            throw new ObjectNotFoundException("Vehicle with UUID: " + vehicleUuid + " was not found!");
//        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        boolean hasAdminRole = authentication.getAuthorities().stream()
//                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
//
//        return hasAdminRole
//                ? vehicleRepository.existsByUuidAndSaleIsNull(vehicleUuid)
//                : vehicleRepository.existsByUuidAndOwner_EmailAndSaleIsNull(vehicleUuid, authentication.getName());
//    }

    private UserEditDTO mapToUserEditDTO(UserEntity userEntity) {
        return new UserEditDTO()
                .setName(userEntity.getName())
                .setEmail(userEntity.getEmail())
                .setAdmin(userEntity.getRoles().stream().anyMatch(r -> r.getRole().equals(UserRoleEnum.ADMIN)));
//                .setRoles(userEntity.getRoles()
//                        .stream()
//                        .map(RoleEntity::getRole)
//                        .collect(Collectors.toSet()));
    }


}