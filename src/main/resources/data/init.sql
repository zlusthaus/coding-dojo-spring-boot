create user weatheruser with encrypted password 'myweatherpass';
grant all on database weatherapp to weatheruser;
create schema weathersch;
grant all on schema weathersch to weatheruser;

-- and now run the schema.sql..
