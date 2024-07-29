1st API/Function that, for each file provided, returns 10 consecutive data points starting from a random
timestamp.

example calls:

GET: http://localhost:8080/exportCSV/LSEG/0 
GET: http://localhost:8080/exportCSV/LSEG/1

0 is the first csv file from the input LSEG/NASDAQ/NYSE folders, 1 is the second CSV file

============================================================================================================
2nd API/function that gets the output from 1st one and predicts the next 3 values in the timeseries data.

example calls: 

POST: http://localhost:8080/predictions

with BODY JSON:
[

        {
            "stockExchange":"LSEG",
             "numberOfFiles":2
             
        }
       , 
        {
            "stockExchange":"NASDAQ",
            "numberOfFiles": 1
        }
        ,
         {
            "stockExchange":"NYSE",
            "numberOfFiles":2 
        }
  ]

=================================================================================================================================
Before calling the second API delete the predictions folder from the output. This folder is generated as an outcome of the 2nd API call. It will be generated as a result

This project is realized in Spring Boot Framework, In order to run it it will be needed:
-Import the generated project into your IDE.
-Maven: Run mvn clean install in the project's root directory.
- From IDE:
Locate the main method in the Application class.
Right-click on the main method and select "Run" or "Debug".
  
