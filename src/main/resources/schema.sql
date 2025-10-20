-- PostGIS spatial indexing
CREATE INDEX IF NOT EXISTS idx_location_geom
ON location
USING GIST (geometry);