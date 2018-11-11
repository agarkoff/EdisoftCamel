CREATE TABLE source_order
(
  id           BIGSERIAL PRIMARY KEY,
  order_number TEXT,
  xml          TEXT,
  date         timestamp with time zone
);

CREATE TABLE dest_order
(
  id              BIGSERIAL PRIMARY KEY,
  source_order_id INTEGER REFERENCES source_order (id) ON DELETE CASCADE,
  order_number    TEXT,
  xml             TEXT,
  date            timestamp with time zone
);
