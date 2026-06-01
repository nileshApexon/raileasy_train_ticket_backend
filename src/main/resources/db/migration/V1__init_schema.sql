CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trains (
    id UUID PRIMARY KEY,
    train_number VARCHAR(20) NOT NULL UNIQUE,
    train_name VARCHAR(150) NOT NULL,
    seats_per_class INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE schedules (
    id UUID PRIMARY KEY,
    train_id UUID NOT NULL,
    from_station VARCHAR(120) NOT NULL,
    to_station VARCHAR(120) NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    journey_date DATE NOT NULL,
    fare_sleeper DECIMAL(10,2) NOT NULL,
    fare_ac3 DECIMAL(10,2) NOT NULL,
    fare_ac2 DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedule_train FOREIGN KEY (train_id) REFERENCES trains(id)
);

CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    schedule_id UUID NOT NULL,
    travel_class VARCHAR(20) NOT NULL,
    seat_numbers VARCHAR(255) NOT NULL,
    pnr_number VARCHAR(32) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);

CREATE INDEX idx_schedule_search ON schedules(from_station, to_station, journey_date);
CREATE INDEX idx_booking_schedule_class_status ON bookings(schedule_id, travel_class, status);

