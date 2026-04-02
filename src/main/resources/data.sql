INSERT INTO drone (serial_number, model, weight_limit, battery_capacity, state)
VALUES
    ('DRN-10001', 'LIGHTWEIGHT', 200, 80, 'IDLE'),
    ('DRN-10002', 'MIDDLEWEIGHT', 400, 90, 'IDLE'),
    ('DRN-10003', 'CRUISERWEIGHT', 600, 50, 'IDLE'),
    ('DRN-10004', 'MIDDLEWEIGHT', 450, 70, 'IDLE'),
    ('DRN-10005', 'CRUISERWEIGHT', 650, 60, 'IDLE'),
    ('DRN-10006', 'HEAVYWEIGHT', 800, 30, 'IDLE');
INSERT INTO medication (name, weight, code, image)
VALUES
    ('MED-A', 50, 'MED_A_01', NULL),
    ('MED-B', 120, 'MED_B_02', NULL),
    ('MED-C', 200, 'MED_C_03', NULL),
    ('MED-D', 300, 'MED_D_04', NULL);