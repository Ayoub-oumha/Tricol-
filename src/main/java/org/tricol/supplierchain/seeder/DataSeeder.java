package org.tricol.supplierchain.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.entity.Permission;
import org.tricol.supplierchain.entity.RoleApp;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.enums.PermissionName;
import org.tricol.supplierchain.enums.RoleName;
import org.tricol.supplierchain.repository.PermissionRepository;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Starting data seeding...");

        seedPermissions();
        seedRoles();
        seedUsers();

        log.info("Data seeding completed successfully!");
    }

    private void seedPermissions() {
        if (permissionRepository.count() > 0) {
            log.info("Permissions already seeded, skipping...");
            return;
        }

        log.info("Seeding permissions...");

        // Fournisseur permissions
        createPermission(PermissionName.FOURNISSEUR_CREATE, "Create supplier", "FOURNISSEUR", "CREATE");
        createPermission(PermissionName.FOURNISSEUR_UPDATE, "Update supplier", "FOURNISSEUR", "UPDATE");
        createPermission(PermissionName.FOURNISSEUR_DELETE, "Delete supplier", "FOURNISSEUR", "DELETE");
        createPermission(PermissionName.FOURNISSEUR_READ, "Read supplier", "FOURNISSEUR", "READ");

        // Produit permissions
        createPermission(PermissionName.PRODUIT_CREATE, "Create product", "PRODUIT", "CREATE");
        createPermission(PermissionName.PRODUIT_UPDATE, "Update product", "PRODUIT", "UPDATE");
        createPermission(PermissionName.PRODUIT_DELETE, "Delete product", "PRODUIT", "DELETE");
        createPermission(PermissionName.PRODUIT_READ, "Read product", "PRODUIT", "READ");
        createPermission(PermissionName.PRODUIT_CONFIGURE_SEUIL, "Configure product threshold", "PRODUIT", "CONFIGURE_SEUIL");

        // Commande permissions
        createPermission(PermissionName.COMMANDE_CREATE, "Create order", "COMMANDE", "CREATE");
        createPermission(PermissionName.COMMANDE_UPDATE, "Update order", "COMMANDE", "UPDATE");
        createPermission(PermissionName.COMMANDE_VALIDATE, "Validate order", "COMMANDE", "VALIDATE");
        createPermission(PermissionName.COMMANDE_CANCEL, "Cancel order", "COMMANDE", "CANCEL");
        createPermission(PermissionName.COMMANDE_RECEIVE, "Receive order", "COMMANDE", "RECEIVE");
        createPermission(PermissionName.COMMANDE_READ, "Read order", "COMMANDE", "READ");

        // Stock permissions
        createPermission(PermissionName.STOCK_READ, "Read stock", "STOCK", "READ");
        createPermission(PermissionName.STOCK_VALORISATION, "Stock valuation", "STOCK", "VALORISATION");
        createPermission(PermissionName.STOCK_HISTORIQUE, "Stock history", "STOCK", "HISTORIQUE");

        // Bon de sortie permissions
        createPermission(PermissionName.BON_SORTIE_CREATE, "Create exit voucher", "BON_SORTIE", "CREATE");
        createPermission(PermissionName.BON_SORTIE_VALIDATE, "Validate exit voucher", "BON_SORTIE", "VALIDATE");
        createPermission(PermissionName.BON_SORTIE_CANCEL, "Cancel exit voucher", "BON_SORTIE", "CANCEL");
        createPermission(PermissionName.BON_SORTIE_READ, "Read exit voucher", "BON_SORTIE", "READ");

        // User management and audit permissions
        createPermission(PermissionName.USER_MANAGE, "Manage users", "USER", "MANAGE");
        createPermission(PermissionName.AUDIT_READ, "Read audit logs", "AUDIT", "READ");

        log.info("Permissions seeded successfully!");
    }

    private void createPermission(PermissionName name, String description, String resource, String action) {
        Permission permission = Permission.builder()
                .name(name)
                .description(description)
                .resource(resource)
                .action(action)
                .build();
        permissionRepository.save(permission);
    }

    private void seedRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles already seeded, skipping...");
            return;
        }

        log.info("Seeding roles...");

        // ADMIN - Full access
        Set<PermissionName> adminPermissions = new HashSet<>(Arrays.asList(PermissionName.values()));
        createRole(RoleName.ADMIN, "Administrator with full access", adminPermissions);

        // RESPONSABLE_ACHATS - Purchasing manager
        Set<PermissionName> responsableAchatsPermissions = new HashSet<>(Arrays.asList(
                PermissionName.FOURNISSEUR_CREATE,
                PermissionName.FOURNISSEUR_UPDATE,
                PermissionName.FOURNISSEUR_READ,
                PermissionName.PRODUIT_READ,
                PermissionName.PRODUIT_CONFIGURE_SEUIL,
                PermissionName.COMMANDE_CREATE,
                PermissionName.COMMANDE_UPDATE,
                PermissionName.COMMANDE_VALIDATE,
                PermissionName.COMMANDE_CANCEL,
                PermissionName.COMMANDE_READ,
                PermissionName.STOCK_READ,
                PermissionName.STOCK_VALORISATION,
                PermissionName.STOCK_HISTORIQUE
        ));
        createRole(RoleName.RESPONSABLE_ACHATS, "Purchasing manager", responsableAchatsPermissions);

        // MAGASINIER - Warehouse keeper
        Set<PermissionName> magasinierPermissions = new HashSet<>(Arrays.asList(
                PermissionName.PRODUIT_READ,
                PermissionName.COMMANDE_READ,
                PermissionName.COMMANDE_RECEIVE,
                PermissionName.STOCK_READ,
                PermissionName.STOCK_HISTORIQUE,
                PermissionName.BON_SORTIE_CREATE,
                PermissionName.BON_SORTIE_VALIDATE,
                PermissionName.BON_SORTIE_READ
        ));
        createRole(RoleName.MAGASINIER, "Warehouse keeper", magasinierPermissions);

        // CHEF_ATELIER - Workshop manager
        Set<PermissionName> chefAtelierPermissions = new HashSet<>(Arrays.asList(
                PermissionName.PRODUIT_READ,
                PermissionName.STOCK_READ,
                PermissionName.BON_SORTIE_CREATE,
                PermissionName.BON_SORTIE_READ
        ));
        createRole(RoleName.CHEF_ATELIER, "Workshop manager", chefAtelierPermissions);

        log.info("Roles seeded successfully!");
    }

    private void createRole(RoleName roleName, String description, Set<PermissionName> permissionNames) {
        Set<Permission> permissions = new HashSet<>();
        for (PermissionName permissionName : permissionNames) {
            permissionRepository.findByName(permissionName)
                    .ifPresent(permissions::add);
        }

        RoleApp role = RoleApp.builder()
                .name(roleName)
                .description(description)
                .permissions(permissions)
                .build();
        roleRepository.save(role);
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already seeded, skipping...");
            return;
        }

        log.info("Seeding users...");

        // Create Admin user
        RoleApp adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        createUser("admin", "admin@tricol.com", "admin123", "System Administrator", adminRole, true);

        // Create Responsable Achats user
        RoleApp responsableAchatsRole = roleRepository.findByName(RoleName.RESPONSABLE_ACHATS)
                .orElseThrow(() -> new RuntimeException("Responsable Achats role not found"));
        createUser("responsable", "responsable@tricol.com", "responsable123", "Jean Dupont", responsableAchatsRole, true);

        // Create Magasinier user
        RoleApp magasinierRole = roleRepository.findByName(RoleName.MAGASINIER)
                .orElseThrow(() -> new RuntimeException("Magasinier role not found"));
        createUser("magasinier", "magasinier@tricol.com", "magasinier123", "Marie Martin", magasinierRole, true);

        // Create Chef Atelier user
        RoleApp chefAtelierRole = roleRepository.findByName(RoleName.CHEF_ATELIER)
                .orElseThrow(() -> new RuntimeException("Chef Atelier role not found"));
        createUser("chef_atelier", "chef.atelier@tricol.com", "chef123", "Pierre Bernard", chefAtelierRole, true);

        log.info("Users seeded successfully!");
    }

    private void createUser(String username, String email, String password, String fullName,
                           RoleApp role, boolean enabled) {
        UserApp user = UserApp.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .enabled(enabled)
                .locked(false)
                .build();
        userRepository.save(user);
        log.info("Created user: {} with role: {}", username, role.getName());
    }
}

