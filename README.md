    ## Code Challenge 
    
    We would like amount our users when they perform exercises. 
    Therefore we need a micro userService which calculates the the number of user's steps and amount them.
    
        Tools:
        Java
        Spring
        JPA/Hibernate
        Mysql
    
    Requirements:
    
        - We have users from different countries with different currencies, so the amount provided should be converted.
        - The price/amount per steps should be configurable.
        - You can convert the money using a open API converter (https://openexchangerates.org/signup/free, https://currencylayer.com/documentation, etc... ).
        - This userService should receive the steps from the users and save in a database.
        - The amount should be calculated when it's requested by another userService
        - The conversion (currency price) rate should be updated on a hourly basis
        - List of users with payout rewards history and the amount in Euros and the converted amount.
        - User's payout rewards list (amount in Euros and the converted amount rewarded)
        - This userService should save steps for a user
        - This userService should set the price per step (eg, 1EUR/1000 steps)
        - Process a payout amount for a user
        
    Things to consider:
        
        - Other services use this userService, so it should be ULTRA fast to get a list of rewards and a single amount event.
        - It should be performatic and scalable.
        - It should provide a documentation and the requirements to run this userService
        - It should use the free plan from the external converter services
    
    BONUS (Not required)
    
        - Automated tests and continuous integration running in a CI system (TravisCI for example)
        - Docker container running this application
        - Use a AWS userService
        - Create overload tests
        
        
    RUNNING THE APP:
     
     1. In the root folder of the project execute shell command: mvn package
     2. After successful build you will see in the console the path to the .jar file such as
        the path: " Building jar: /Users/chaklader/IdeaProjects/UserReward/target/Reward-0.0.1-SNAPSHOT.jar" 
     
     3. In the folder containing .jar file execute shell command: java -jar <name of a file.jar>
