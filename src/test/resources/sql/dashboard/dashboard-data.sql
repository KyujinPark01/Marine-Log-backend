INSERT INTO organization (org_id, name, location, description, created_at, updated_at)
VALUES (1, 'Ocean Savers', 'Seoul', 'Marine cleanup org', NOW(), NOW()),
       (2, 'River Keepers', 'Busan', 'River cleanup org', NOW(), NOW());

INSERT INTO member (user_id, email, name, address, phone_number, role, refresh_token,
                    refresh_token_expires_at, organization_id, created_at, updated_at)
VALUES (10, 'captain@oceansavers.org', 'Captain', 'Seoul', '010-1111-2222', 'HOST',
        NULL, NULL, 1, NOW(), NOW());

INSERT INTO activity (activity_id, user_id, name, description, activity_date,
                      start_time, end_time, participants, status, weight_kg, volume_L,
                      created_at, updated_at)
VALUES (100, 10, 'Beach Cleanup March', 'Cleaned the east beach', '2024-03-20',
        '08:00:00', '11:00:00', 20, 'PUBLISHED', 30.0, 6.0, NOW(), NOW()),
       (101, 10, 'Beach Cleanup April', 'Cleaned the west beach', '2024-04-18',
        '09:00:00', '12:00:00', 24, 'PUBLISHED', 45.0, 8.5, NOW(), NOW()),
       (102, 10, 'River Cleanup May', 'Busan riverside cleanup', '2024-05-05',
        '07:30:00', '10:30:00', 18, 'PUBLISHED', 28.0, 5.0, NOW(), NOW());

INSERT INTO hosted_by (activity_id, org_id, contribution)
VALUES (100, 1, 1.0),
       (101, 1, 1.0),
       (102, 2, 1.0);

INSERT INTO location (activity_id, geometry, region1depth_name, region2depth_name,
                      region3depth_name, address, created_at, updated_at)
VALUES (100, ST_SetSRID(ST_MakePoint(126.9780, 37.5665), 4326),
        'Seoul', 'Jongno-gu', 'Gwanghwamun', 'Seoul Center', NOW(), NOW()),
       (101, ST_SetSRID(ST_MakePoint(126.9076, 37.5193), 4326),
        'Seoul', 'Yeongdeungpo-gu', 'Yeouido', 'Seoul West', NOW(), NOW()),
       (102, ST_SetSRID(ST_MakePoint(129.0756, 35.1796), 4326),
        'Busan', 'Jung-gu', 'Yeongju-dong', 'Busan Port', NOW(), NOW());

INSERT INTO waste (waste_id, activity_id, specific_type, type, picture_url, weight_kg, volume_L)
VALUES (1000, 100, 'Bottle', 'Plastic', NULL, 10.0, 3.0),
       (1001, 100, 'Can', 'Metal', NULL, 5.0, 2.0),
       (1002, 101, 'Net', 'Plastic', NULL, 15.0, 4.5),
       (1003, 101, 'Glass shards', 'Glass', NULL, 10.0, 3.0),
       (1004, 102, 'Rope', 'Plastic', NULL, 12.0, 2.0),
       (1005, 102, 'Wire', 'Metal', NULL, 5.0, 1.5);
