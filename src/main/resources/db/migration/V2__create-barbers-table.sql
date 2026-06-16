CREATE TABLE barbers (
  user_id BIGINT PRIMARY KEY,
  specialty VARCHAR(100),
  rating DECIMAL(2,1) DEFAULT 5.0,
  avatar VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);