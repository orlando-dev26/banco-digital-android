-- POSTGRES

CREATE TABLE users (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    dni VARCHAR(8) NOT NULL UNIQUE,
    auth0_sub VARCHAR(255) NOT NULL UNIQUE,

    celular VARCHAR(9) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,

    name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,

    role VARCHAR(100) NOT NULL DEFAULT 'USER',
    status VARCHAR(100) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Adicional que se podria integrar siguiendo ejemplos de otras apps bancarias
-- Ocupación
-- Ingreso mensual neto
-- Estado civil

CREATE TABLE products (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(200),
    price NUMERIC(10,2) NOT NULL,
    stock INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at DATE,
    updated_at DATE
);