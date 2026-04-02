DROP TABLE IF EXISTS drone_load;
DROP TABLE IF EXISTS medication;
DROP TABLE IF EXISTS drone;
CREATE TABLE drone (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       serial_number VARCHAR(100) NOT NULL UNIQUE,
                       model VARCHAR(50),
                       weight_limit INT,
                       battery_capacity INT,
                       state VARCHAR(50)
);
CREATE TABLE medication (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name VARCHAR(255),
                            weight INT,
                            code VARCHAR(255),
                            image BLOB
);
CREATE TABLE drone_load (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            drone_id BIGINT,
                            medication_id BIGINT,
                            CONSTRAINT fk_drone FOREIGN KEY (drone_id) REFERENCES drone(id),
                            CONSTRAINT fk_med FOREIGN KEY (medication_id) REFERENCES medication(id)
);