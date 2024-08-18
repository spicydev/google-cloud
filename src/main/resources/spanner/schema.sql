CREATE TABLE artist (
    artist_id INT64 NOT NULL,
    name STRING(MAX)
)PRIMARY KEY (artist_id);

CREATE TABLE album (
    album_id INT64 NOT NULL,
    title STRING(MAX) NOT NULL,
    artist_id INT64 NOT NULL,
    FOREIGN KEY (artist_id)  REFERENCES artist (artist_id)
)PRIMARY KEY (album_id);

CREATE TABLE employee (
    employee_id INT64 NOT NULL,
    last_name STRING(MAX) NOT NULL,
    first_name STRING(MAX) NOT NULL,
    title STRING(MAX),
    reports_to INT64,
    birth_date DATE,
    hire_date DATE,
    address STRING(MAX),
    city STRING(MAX),
    state STRING(MAX),
    country STRING(MAX),
    postal_code STRING(MAX),
    phone STRING(MAX),
    fax STRING(MAX),
    email STRING(MAX),
    FOREIGN KEY (reports_to)  REFERENCES employee (employee_id)
)PRIMARY KEY  (employee_id);

CREATE TABLE customer (
    customer_id INT64 NOT NULL,
    first_name STRING(MAX) NOT NULL,
    last_name STRING(MAX) NOT NULL,
    company STRING(MAX),
    address STRING(MAX),
    city STRING(MAX),
    state STRING(MAX),
    country STRING(MAX),
    postal_code STRING(MAX),
    phone STRING(MAX),
    fax STRING(MAX),
    email STRING(MAX) NOT NULL,
    support_rep_id INT64,
    FOREIGN KEY (support_rep_id)  REFERENCES employee (employee_id)
)PRIMARY KEY  (customer_id);

CREATE TABLE genre (
    genre_id INT64 NOT NULL,
    name STRING(MAX)
)PRIMARY KEY  (genre_id);

CREATE TABLE media_type (
    media_type_id INT64 NOT NULL,
    name STRING(MAX)
)PRIMARY KEY  (media_type_id);

CREATE TABLE invoice (
    invoice_id INT64 NOT NULL,
    customer_id INT64 NOT NULL,
    invoice_date DATE NOT NULL,
    billing_address STRING(MAX),
    billing_city STRING(MAX),
    billing_state STRING(MAX),
    billing_country STRING(MAX),
    billing_postal_code STRING(MAX),
    total FLOAT64 NOT NULL,
    FOREIGN KEY (customer_id)  REFERENCES customer (customer_id)
)PRIMARY KEY  (invoice_id);

CREATE TABLE track (
    track_id INT64 NOT NULL,
    name STRING(MAX) NOT NULL,
    album_id INT64,
    media_type_id INT64 NOT NULL,
    genre_id INT64,
    composer STRING(MAX),
    milliseconds INT64 NOT NULL,
    bytes INT64,
    unit_price FLOAT64 NOT NULL,
    FOREIGN KEY (album_id)  REFERENCES album (album_id),
    FOREIGN KEY (media_type_id)  REFERENCES media_type (media_type_id),
    FOREIGN KEY (genre_id)  REFERENCES genre (genre_id)
) PRIMARY KEY  (track_id);

CREATE TABLE invoice_line (
    invoice_line_id INT64 NOT NULL,
    invoice_id INT64 NOT NULL,
    track_id INT64 NOT NULL,
    unit_price FLOAT64 NOT NULL,
    quantity INT64 NOT NULL,
    FOREIGN KEY (invoice_id)  REFERENCES invoice (invoice_id),
    FOREIGN KEY (track_id)  REFERENCES track (track_id)
)PRIMARY KEY  (invoice_line_id);

CREATE TABLE playlist (
    playlist_id INT64 NOT NULL,
    name STRING(MAX)
)PRIMARY KEY  (playlist_id);

CREATE TABLE playlist_track (
    playlist_id INT64 NOT NULL,
    track_id INT64 NOT NULL,
    FOREIGN KEY (playlist_id)  REFERENCES playlist (playlist_id),
    FOREIGN KEY (track_id)  REFERENCES track (track_id)
) PRIMARY KEY  (playlist_id, track_id);