CREATE TABLE search_results (
    id          bigint PRIMARY KEY,
    url         varchar NOT NULL,
    image_urls  varchar[] NOT NULL
)
