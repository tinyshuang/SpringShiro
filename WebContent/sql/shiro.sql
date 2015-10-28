create database shiro;

create table user(
   name varchar(16) not null PRIMARY KEY,
   pwd  varchar(32) not null
)


create table role(
  id       varchar(32) not null PRIMARY KEY,
  userid   varchar(32) not null,
  roleName varchar(16) not null
)

create table function(
   id       varchar(32) not null PRIMARY KEY,
   userid   varchar(32) not null,
   url      varchar(32) not null
)

select * from function;
select * from role;


insert into user values("admin","123456");
insert into user values("tinys","123456");
insert into user values("role","123456");

insert into role values("1","admin","admin角色");
insert into role values("0","tinys","普调角色");
insert into role values("2","role","role");



insert into function values ("1","admin","/admin");
insert into function values ("2","tinys","needPermission");



