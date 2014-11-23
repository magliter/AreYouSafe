/*****************************************************************
 Script for cleaning the 2012-2014 Crash Dataset for CYCLIST Data 
  1. Clean by selecting the rows of interest 
  2. Remove all values without latitude and longitude information
  2. Removes crashes where no Cyclist were hurt or killed	
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
      ,[NUMBER OF CYCLIST INJURED]
      ,[NUMBER OF CYCLIST KILLED]
      ,[CONTRIBUTING FACTOR VEHICLE 1]
      ,[CONTRIBUTING FACTOR VEHICLE 2]
      ,[UNIQUE KEY]
      ,[VEHICLE TYPE CODE 1]
      ,[VEHICLE TYPE CODE 2]
  FROM [LBS].[dbo].['ALL CRASHES$'] WHERE [LOCATION] IS NOT NULL AND ([NUMBER OF CYCLIST INJURED] >= 1 OR [NUMBER OF CYCLIST KILLED] >= 1) 
   
