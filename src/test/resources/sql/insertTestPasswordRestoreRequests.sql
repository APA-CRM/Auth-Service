INSERT INTO password_restore_request(
    id, verification_code,
    user_id, attempts_count,
    create_at, update_at
)
VALUES
(
    '1988e512-72e4-4982-a554-fb890f863618', '0011',
    101, 0,
    CURRENT_TIME, CURRENT_TIME
),
(
    'c974714f-532f-4844-8d16-731c299a1cf3', '9999',
    102, 5,
    CURRENT_TIME, CURRENT_TIME
);