# -------- таблицы для реализации системных взаимодействий ----------
CREATE TABLE `persistent_logins`
(
    `username` VARCHAR(64) NOT NULL,
    `series` VARCHAR(64) NOT NULL PRIMARY KEY,
    `token` VARCHAR(64) NOT NULL,
    `last_used` TIMESTAMP NOT NULL
);

CREATE TABLE `refresh_token`
(
    `id` BIGINT NOT NULL PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `token` VARCHAR(255) NOT NULL,
    `expiry_date` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP) NOT NULL,
    CONSTRAINT `token_unique`
        UNIQUE (`token`),
    CONSTRAINT `refresh_token_users_id_fk`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

# -------- основные таблицы для РОЛЕВОЙ МОДЕЛИ ----------
CREATE TABLE `users`
(
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(255) DEFAULT 'guest' NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `status` VARCHAR(255) DEFAULT 'ACTIVE' NOT NULL,
    `created_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP) NULL,
    `updated_at` TIMESTAMP DEFAULT (CURRENT_TIMESTAMP) NULL,
    CONSTRAINT `users_email_uindex` UNIQUE (`email`)
)
    COMMENT 'Пользователи';

CREATE TABLE `roles`
(
    `id` BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    `title` VARCHAR(64) NOT NULL,
    CONSTRAINT `roles_title_uindex`
        UNIQUE (`title`)
);

CREATE TABLE `permissions`
(
    `id` BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    `title` VARCHAR(64) NOT NULL,
    CONSTRAINT `permissions_title_uindex`
        UNIQUE (`title`)
);

# ------- промежуточные таблицы -----------
CREATE TABLE `user_role`
(
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    CONSTRAINT `user_roles_roles_id_fk`
        FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `user_roles_users_id_fk`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `role_permission`
(
    `id` BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    `role_id` BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    CONSTRAINT `permissions_roles_id_fk`
        FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `role_permission_permissions_id_fk`
        FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
);
# ------------------
INSERT INTO `roles` (`title`)
VALUES ('ROLE_ADMIN'),
       ('ROLE_USER'),
       ('ROLE_ANONYMOUS');

INSERT INTO `permissions` (`title`)
VALUES ('READ'),
       ('WRITE'),
       ('DELETE');

