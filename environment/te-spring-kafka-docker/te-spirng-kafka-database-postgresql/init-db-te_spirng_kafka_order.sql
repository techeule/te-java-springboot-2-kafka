CREATE USER techeule_orders WITH PASSWORD 'techeule_orders-password';
CREATE DATABASE techeule_orders WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.utf8';
ALTER DATABASE techeule_orders OWNER TO "techeule_orders";


GRANT ALL ON DATABASE techeule_orders TO techeule_orders;
GRANT ALL PRIVILEGES ON DATABASE techeule_orders TO techeule_orders;
