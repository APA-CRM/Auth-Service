INSERT INTO refresh_token(
    id, token,
    expired_at, user_id
)
VALUES
(
    '32ee3ffe-b72a-46d1-9c1d-f087e0399f71', '0L+INmmmYyJDlYkJSr9qGdF+AMY/ye/vJYuxo+uJu1mt4I3fY18OkFwjoMplvT/f+zU',
    CURRENT_TIMESTAMP + INTERVAL '2' DAY, 100
),
(
    'c4ea46cd-0d5a-4fa6-aef4-100ed1f84d08', 'QZWNxib69rfV2RbJy3PRf8hk9OovDKwqaMq39rLu4dc8xsI9m0193mYoRzmXVrYnNzc',
    CURRENT_TIMESTAMP - INTERVAL '2' DAY, 101
);