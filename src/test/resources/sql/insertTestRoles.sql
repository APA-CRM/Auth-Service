INSERT INTO role(
    id, name
) VALUES
(100, 'Admin'),
(200, 'Manager');

INSERT INTO access_control(
   id, role_id, resource, action
) VALUES
(RANDOM_UUID(), 100, 'ALL', 'ALL'),
(RANDOM_UUID(), 200, 'USERS', 'CREATE'),
(RANDOM_UUID(), 200, 'USERS', 'READ'),
(RANDOM_UUID(), 200, 'USERS', 'UPDATE'),
(RANDOM_UUID(), 200, 'USERS', 'DELETE');