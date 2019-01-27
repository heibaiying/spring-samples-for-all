--  建表语句
create table if not exists programmer (
  id       int primary key auto_increment,
  name     varchar(20),
  age      tinyint,
  salary   float,
  birthday datetime
)