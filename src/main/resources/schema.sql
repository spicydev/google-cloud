CREATE TABLE Singers (
  SingerId   INT64 NOT NULL,
  FirstName  STRING(MAX),
  LastName   STRING(MAX),
  BirthDate  DATE
) PRIMARY KEY(SingerId);