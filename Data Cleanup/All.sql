/*****************************************************************
 Script for cleaning the 2012-2014 Crash Dataset  
  1. Clean by selecting the rows of interest 
  2. Remove all values without latitude and longitude information
  2. Removes crashes where noone was hurt or killed	
********************************************************************/
  
SELECT [DATE]
      ,[TIME]
      ,[BOROUGH]
      ,[ZIP CODE]
      ,[LATITUDE]
      ,[LONGITUDE]
      ,[LOCATION]
      ,[ON STREET NAME]
      ,[CROSS STREET NAME]
      ,[OFF STREET NAME]
      ,[NUMBER OF PERSONS INJURED]
      ,[NUMBER OF PERSONS KILLED]
      ,[NUMBER OF PEDESTRIANS INJURED]
      ,[NUMBER OF PEDESTRIANS KILLED]
      ,[NUMBER OF CYCLIST INJURED]
      ,[NUMBER OF CYCLIST KILLED]
      ,[NUMBER OF MOTORIST INJURED]
      ,[NUMBER OF MOTORIST KILLED]
      ,[CONTRIBUTING FACTOR VEHICLE 1]
      ,[CONTRIBUTING FACTOR VEHICLE 2]
      ,[UNIQUE KEY]
      ,[VEHICLE TYPE CODE 1]
      ,[VEHICLE TYPE CODE 2]
  FROM [LBS].[dbo].['ALL CRASHES$'] WHERE [LOCATION] IS NOT NULL AND ([NUMBER OF PERSONS INJURED] >= 1 OR [NUMBER OF PERSONS KILLED] >= 1)
   


 