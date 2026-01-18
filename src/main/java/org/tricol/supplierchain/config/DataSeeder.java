package org.tricol.supplierchain.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.entity.Permission;
import org.tricol.supplierchain.entity.Role;
import org.tricol.supplierchain.entity.RolePermission;
import org.tricol.supplierchain.repository.PermissionRepository;
import org.tricol.supplierchain.repository.RolePermissionRepository;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (permissionRepository.count() > 0) {
            log.info("Database already seeded. Skipping seed data.");
            return;
        }

        log.info("Starting database seeding...");

        List<Role> roles = seedRoles();

        List<Permission> permissions = seedPermissions();

        seedRolePermissions(roles, permissions);

        log.info("Database seeding completed successfully!");
    }

    private List<Role> seedRoles() {
        log.info("Seeding roles...");
        
        List<Role> roles = new ArrayList<>();
        roles.add(createRole("ADMIN", "Administrator with full access"));
        roles.add(createRole("RESPONSABLE_ACHATS", "Purchasing Manager"));
        roles.add(createRole("MAGASINIER", "Warehouse Manager"));
        roles.add(createRole("CHEF_ATELIER", "Workshop Manager"));
        
        roles = roleRepository.saveAll(roles);
        log.info("Seeded {} roles", roles.size());
        
        return roles;
    }

    private Role createRole(String name, String description) {
        return Role.builder()
                .name(name)
                .description(description)
                .build();
    }

    private List<Permission> seedPermissions() {
        log.info("Seeding permissions...");
        
        List<Permission> permissions = new ArrayList<>();

        // FOURNISSEUR Permissions
        permissions.add(createPermission("FOURNISSEUR_CREATE", "Créer/Modifier/Supprimer fournisseur", "FOURNISSEUR", "WRITE"));
        permissions.add(createPermission("FOURNISSEUR_READ", "Consulter fournisseur", "FOURNISSEUR", "READ"));

        // PRODUIT Permissions
        permissions.add(createPermission("PRODUIT_CREATE", "Créer/Modifier/Supprimer produit", "PRODUIT", "WRITE"));
        permissions.add(createPermission("PRODUIT_READ", "Consulter produit", "PRODUIT", "READ"));
        permissions.add(createPermission("PRODUIT_CONFIGURE_ALERT", "Configurer seuils d'alerte", "PRODUIT", "CONFIGURE"));

        // COMMANDE FOURNISSEUR Permissions
        permissions.add(createPermission("COMMANDE_CREATE", "Créer/Modifier commande fournisseur", "COMMANDE", "WRITE"));
        permissions.add(createPermission("COMMANDE_VALIDATE", "Valider commande fournisseur", "COMMANDE", "VALIDATE"));
        permissions.add(createPermission("COMMANDE_CANCEL", "Annuler commande fournisseur", "COMMANDE", "CANCEL"));
        permissions.add(createPermission("COMMANDE_RECEIVE", "Réceptionner commande fournisseur", "COMMANDE", "RECEIVE"));
        permissions.add(createPermission("COMMANDE_READ", "Consulter commande fournisseur", "COMMANDE", "READ"));

        // STOCK & LOTS Permissions
        permissions.add(createPermission("STOCK_READ", "Consulter stock/lots", "STOCK", "READ"));
        permissions.add(createPermission("STOCK_VALUATION_READ", "Voir valorisation FIFO", "STOCK", "VALUATION"));
        permissions.add(createPermission("STOCK_HISTORY_READ", "Consulter historique mouvements", "STOCK", "HISTORY"));

        // BON DE SORTIE Permissions
        permissions.add(createPermission("BON_SORTIE_CREATE", "Créer bon de sortie (brouillon)", "BON_SORTIE", "WRITE"));
        permissions.add(createPermission("BON_SORTIE_VALIDATE", "Valider bon de sortie", "BON_SORTIE", "VALIDATE"));
        permissions.add(createPermission("BON_SORTIE_CANCEL", "Annuler bon de sortie", "BON_SORTIE", "CANCEL"));
        permissions.add(createPermission("BON_SORTIE_READ", "Consulter bon de sortie", "BON_SORTIE", "READ"));

        // ADMINISTRATION Permissions
        permissions.add(createPermission("USER_MANAGE", "Gérer utilisateurs", "USER", "MANAGE"));
        permissions.add(createPermission("AUDIT_VIEW", "Voir logs d'audit", "AUDIT", "VIEW"));

        permissions = permissionRepository.saveAll(permissions);
        log.info("Seeded {} permissions", permissions.size());
        
        return permissions;
    }

    private Permission createPermission(String name, String description, String resource, String action) {
        return Permission.builder()
                .name(name)
                .description(description)
                .resource(resource)
                .action(action)
                .build();
    }

    private void seedRolePermissions(List<Role> roles, List<Permission> permissions) {
        log.info("Seeding role permissions...");

        List<RolePermission> rolePermissions = new ArrayList<>();

        // Find roles by name
        Role adminRole = findRoleByName(roles, "ADMIN");
        Role responsableAchatsRole = findRoleByName(roles, "RESPONSABLE_ACHATS");
        Role magasinierRole = findRoleByName(roles, "MAGASINIER");
        Role chefAtelierRole = findRoleByName(roles, "CHEF_ATELIER");

        // ADMIN - All permissions
        for (Permission permission : permissions) {
            rolePermissions.add(createRolePermission(adminRole, permission));
        }

        // RESPONSABLE_ACHATS - Based on permissions matrix
        rolePermissions.addAll(createRolePermissionsForRole(responsableAchatsRole, permissions,
                "FOURNISSEUR_CREATE", "FOURNISSEUR_READ",
                "PRODUIT_CREATE", "PRODUIT_READ", "PRODUIT_CONFIGURE_ALERT",
                "COMMANDE_CREATE", "COMMANDE_VALIDATE", "COMMANDE_CANCEL", "COMMANDE_READ",
                "STOCK_READ", "STOCK_VALUATION_READ", "STOCK_HISTORY_READ",
                "BON_SORTIE_READ"
        ));

        // MAGASINIER - Based on permissions matrix
        rolePermissions.addAll(createRolePermissionsForRole(magasinierRole, permissions,
                "FOURNISSEUR_READ",
                "PRODUIT_READ",
                "COMMANDE_RECEIVE", "COMMANDE_READ",
                "STOCK_READ", "STOCK_VALUATION_READ", "STOCK_HISTORY_READ",
                "BON_SORTIE_CREATE", "BON_SORTIE_VALIDATE", "BON_SORTIE_CANCEL", "BON_SORTIE_READ"
        ));

        // CHEF_ATELIER - Based on permissions matrix
        rolePermissions.addAll(createRolePermissionsForRole(chefAtelierRole, permissions,
                "PRODUIT_READ",
                "STOCK_READ", "STOCK_HISTORY_READ",
                "BON_SORTIE_CREATE", "BON_SORTIE_READ"
        ));

        rolePermissionRepository.saveAll(rolePermissions);
        log.info("Seeded {} role permissions", rolePermissions.size());
    }

    private Role findRoleByName(List<Role> roles, String name) {
        return roles.stream()
                .filter(role -> role.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role not found: " + name));
    }

    private RolePermission createRolePermission(Role role, Permission permission) {
        return RolePermission.builder()
                .role(role)
                .permission(permission)
                .build();
    }

    private List<RolePermission> createRolePermissionsForRole(Role role, List<Permission> allPermissions, String... permissionNames) {
        List<RolePermission> rolePermissions = new ArrayList<>();
        
        for (String permissionName : permissionNames) {
            Permission permission = allPermissions.stream()
                    .filter(p -> p.getName().equals(permissionName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Permission not found: " + permissionName));
            
            rolePermissions.add(createRolePermission(role, permission));
        }
        
        return rolePermissions;
    }



}
