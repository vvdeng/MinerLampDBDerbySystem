create table usr(id bigint primary key generated always as identity,name varchar(20),pwd varchar(20),priv int );
create table staff(id bigint primary key generated always as identity,name varchar(20),work_id bigint,sex int,birth_date varchar(10),certificate_no varchar(20),edu_level varchar(10),phone varchar(20), address varchar(50),rack_id bigint,lamp_no bigint,profession_id bigint, profession varchar(20),department_id bigint,department varchar(20),clazz_id bigint,clazz varchar(20),image blob,charging_lasttime bigint);
create table info_item(id bigint primary key generated always as identity,name varchar(20),type int,value varchar(20));
create table lamp_unit(rack_id bigint,lamp_no bigint,staff_name varchar(20),staff_depart varchar(20), staff_worktype varchar(20), name varchar(200), state int,charging_counts int, charging_begintime bigint,charging_lasttime bigint,charging_total_time bigint default 0,manufacturer varchar(20),primary key(rack_id,lamp_no));
create table lamp_rack(id bigint primary key,name varchar(20));
create table led_setting(id bigint primary key,content varchar(200),display int);
create table lamp_change_log(id bigint generated always as identity primary key,rack_id bigint,lamp_no bigint,work_id bigint,staff_name varchar(20),change_time bigint,operation int);
CREATE table charging_log(id BIGINT generated always as identity PRIMARY KEY,work_id BIGINT,NAME VARCHAR(20),rack_id BIGINT,lamp_no BIGINT,profession VARCHAR(20),department VARCHAR(20),clazz_id BIGINT,clazz VARCHAR(20),underground_begintime bigint,underground_endtime bigint,old_state int,new_state int, action int,description VARCHAR(200),last_update_time bigint ,clazz_day char(10)); 
create table rack_statistics(id bigint  primary key,idle_count bigint,charging_count bigint default 0,underground_count bigint default 0,error_count bigint default 0,full_count bigint default 0);
create table sysinf(id bigint primary key generated always as identity,cat int ,val int ,descr varchar(20));
insert into usr(name,pwd,priv) values('admin','admin',1);
insert into info_item(name,type,value) values( '教育程度',0,'高中');
insert into info_item(name,type,value) values( '教育程度',0,'专科');
insert into info_item(name,type,value) values( '教育程度',0,'本科');
insert into info_item(name,type,value) values( '工种',1,'综采');
insert into info_item(name,type,value)  values( '工种',1,'掘进');
insert into info_item(name,type,value)  values( '工种',1,'机电');
insert into info_item(name,type,value)  values( '部门',2,'生产部');
insert into info_item(name,type,value)  values( '部门',2,'运输部');
insert into info_item(name,type,value)  values( '部门',2,'综合部');
insert into info_item(name,type,value)  values( '班次',3,'8点');
insert into info_item(name,type,value)  values( '班次',3,'16点');
insert into info_item(name,type,value)  values( '班次',3,'0点');
insert into sysinf(cat,val) values(1,1);
insert into sysinf(cat,val) values(2,300);
insert into sysinf(cat,val) values(3,30);