CREATE TABLE location_zones
(
  zone_code          VARCHAR(20) PRIMARY KEY,
  name               VARCHAR(100) NOT NULL,
  centroid_latitude  FLOAT        NOT NULL,
  centroid_longitude FLOAT        NOT NULL,
  range              FLOAT        NOT NULL
);