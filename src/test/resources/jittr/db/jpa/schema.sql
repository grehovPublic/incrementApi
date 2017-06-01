drop table if exists jittle;
drop table if exists jitter;
drop sequence if exists jittles_seq;
drop sequence if exists jitters_seq;

create sequence jittles_seq;
create sequence jitters_seq;

create table jitter (
    id integer default jitters_seq.nextval primary key,
    username varchar(32) not null,
    password varchar(60) not null,
    role varchar(16) not null,
    fullName varchar(100) not null,
    email varchar(50) not null
);

create table jittle (
    id integer primary key,
    jitter integer not null,
    message varchar(140) not null,
    postedTime datetime not null,
    author varchar(32) not null,
    judgment varchar(16) not null,
    tqueue varchar(16) not null,
    country varchar(32),
    latitude float,
    longitude float,
    foreign key (jitter) references jitter(id)
);
