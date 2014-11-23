/*****************************************************************
 Script for cleaning the 2012-2014 Crash Dataset for PEDESTRIAN Data 
  1. Clean by selecting the rows of interest 
  2. Remove all values without latitude and longitude information
  2. Removes crashes where no pedestrian were hurt or killed	
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
      ,[NUMBER OF PEDESTRIANS INJURED]
      ,[NUMBER OF PEDESTRIANS KILLED]
      ,[CONTRIBUTING FACTOR VEHICLE 1]
      ,[UNIQUE KEY]
      ,[VEHICLE TYPE CODE 1]
      ,[VEHICLE TYPE CODE 2]
  FROM [LBS].[dbo].['ALL CRASHES$'] WHERE [LOCATION] IS NOT NULL AND ([NUMBER OF PEDESTRIANS INJURED] >= 1 OR [NUMBER OF PEDESTRIANS KILLED] >= 1) 
   
