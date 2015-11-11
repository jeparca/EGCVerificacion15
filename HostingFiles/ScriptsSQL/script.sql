/*Las lineas comentadas se usaban para crear la base de datos local
  pero ya no son nesecarias pues el despliegue se realiza en HOSTINGER*/
/*start transaction;
drop user 'admin'@'%';
drop database if exists `keysvotes`;
create database `keysvotes`;
use `keysvotes`;
create user 'admin'@'%' identified by 'admin';


grant select, insert, update, delete, create, drop, references, index, alter,
create temporary tables, lock tables, create view, create routine,
alter routine, execute, trigger, show view
on `keysvotes`.* to 'admin'@'%';
*/

CREATE TABLE `keysvotes` (
`idvotation` VARCHAR(128) UNIQUE NOT NULL,
`publicKey` VARCHAR(2048) NOT NULL,
`privateKey` VARCHAR(2048) NOT NULL
);
CREATE TABLE `keysvotesAES` (
`idvotation` VARCHAR(128) UNIQUE NOT NULL,
`secretKey` VARCHAR(256) NOT NULL
);


/*commit;*/