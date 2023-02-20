create database techeule_orders;
create user 'techeule_orders'@'%' identified WITH mysql_native_password by 'techeule_orders-password';
GRANT ALL PRIVILEGES ON techeule_orders.* TO 'techeule_orders'@'%';
