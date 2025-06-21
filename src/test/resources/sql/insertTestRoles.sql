INSERT INTO role(
    id, name, is_deletable
) VALUES
(100, 'Admin', false),
(200, 'Manager', true);

INSERT INTO access_control(
   id, role_id, resource, action
) VALUES
(RANDOM_UUID(), 100, 'ALL', 'ALL'),
(RANDOM_UUID(), 200, 'USERS', 'CREATE'),
(RANDOM_UUID(), 200, 'USERS', 'READ'),
(RANDOM_UUID(), 200, 'USERS', 'UPDATE'),
(RANDOM_UUID(), 200, 'USERS', 'DELETE');