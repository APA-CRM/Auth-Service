INSERT INTO refresh_token(id, token,
                          expired_at, user_id, device_info,
                          created_at, updated_at)
VALUES ('32ee3ffe-b72a-46d1-9c1d-f087e0399f71', '0L+INmmmYyJDlYkJSr9qGdF+AMY/ye/vJYuxo+uJu1mt4I3fY18OkFwjoMplvT/f+zU',
        CURRENT_TIMESTAMP + INTERVAL '2' DAY, 100, 'Firefox 122.0 Windows NT Desktop',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('c4ea46cd-0d5a-4fa6-aef4-100ed1f84d08', 'QZWNxib69rfV2RbJy3PRf8hk9OovDKwqaMq39rLu4dc8xsI9m0193mYoRzmXVrYnNzc',
        CURRENT_TIMESTAMP - INTERVAL '2' DAY, 101, 'Chrome 121 Android Google Pixel 8',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);