INSERT INTO user_(
    id, login, email,
    password,
    first_name, last_name, full_name,
    created_at, update_at
)
VALUES
(
    100, 'Wrong',
    'test@gmail.com', 'p9gnbe123',
    'Alex', 'Wong', 'Alex Wong',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- Password: TepydIV^nG&&N4V
(
    101, 'LoginUser', 'test2@gmail.com',
    '$2a$10$T6Cff.k0MNm.VvO11TaVjuZZTsoaBOMF5VeU0LmHD.TeIM9vts3mu',
    'Serious', 'Sam', 'Serious Sam',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    102, 'Test_User', 'email@gmail.com',
    '$2a$10$T6Cff.k0MNm.VvO11TaVjuZZTsoaBOMF5VeU0LmHD.TeIM9vts3mu',
    'Max', 'Payne', 'Max Payne',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);