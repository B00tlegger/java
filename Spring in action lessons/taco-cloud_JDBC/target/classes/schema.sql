create table if not exists ingredient (
id varchar(4) not null,
name varchar(25) not null,
type varchar(10) not null
);

create TABLE if NOT EXISTS taco (
id identity,
name varchar(50) NOT NULL,
createAt timestamp NOT NULL
);

create TABLE if NOT EXISTS taco_ingredients (
taco bigint NOT NULL,
ingredient varchar(4) NOT NULL
);

alter table taco_Ingredients add foreign key (taco) REFERENCES taco(id);
alter table taco_Ingredients add foreign key (ingredient) REFERENCES ingredient(id);

create TABLE if NOT EXISTS taco_order (
id identity,
deliveryName varchar(50) NOT NULL,
deliveryStreet varchar(50) NOT NULL,
deliveryCity varchar(50) NOT NULL,
deliveryState varchar(50) NOT NULL,
deliveryZip varchar(10) NOT NULL,
ccNumber varchar(16) NOT NULL,
ccExpiration varchar(5) NOT NULL,
ccCVV varchar(3) NOT NULL,
placedAt timestamp NOT NULL
);

CREATE TABLE if NOT EXISTS taco_order_tacos (
taco_order bigint NOT NULL,
taco bigint NOT NULL
);

alter table taco_order_tacos add foreign key (taco_order) REFERENCES taco_order(id);
alter table taco_order_tacos add foreign key (taco) REFERENCES taco(id);
