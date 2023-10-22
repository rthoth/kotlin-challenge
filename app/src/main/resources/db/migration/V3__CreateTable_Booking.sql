create table BOOKING (
    ID varchar(255) primary key not null,
    PHONE_ID varchar(255) not null,
    MEMBER_ID varchar(255) not null,
    STARTING timestamp not null,
    ENDING timestamp not null
);