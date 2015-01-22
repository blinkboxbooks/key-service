CREATE TABLE `keys` (
  `id`        VARCHAR(32) NOT NULL PRIMARY KEY,
  `isbn`      VARCHAR(13) NOT NULL,
  `key`       VARCHAR(32) NOT NULL,
  `file_hash` VARCHAR(32) NOT NULL,
  `v1_path`   VARCHAR(64),
  UNIQUE KEY `version` (`isbn`, `file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO db_version (db_version_id, file_name, jira_issue)
VALUES (2, '002-create-keys-table.sql', 'MV-329');