Sprint week 5
Tasks Gijs:
- `Issue 2`: Applicant information endpoints and logic <br />
	Issues encountered: Lack in DTOs, we had to set up a shared package which Iarina did for us.<br />
	<br />
	Estimated time: 2-3 hours.<br />
	Actual time spent: 4 hours. <br />
	
- `Issue 7`: Way of retrieving ratings for students' previous TA experience. <br />
	Issues encountered: There was a different in opinion about what the rating should be. I personally opted for the average of all the jobs a TA has worked. Also there was some errors in testing the TA controller, which were fixed after our TA suggested the SpringBootTest annotation.<br />
	<br />
	Estimated time: 2-3 hours.<br />
	Actual time spent: 4 hours. <br />
	
- `Issue 8`:  Have endpoints and logic for letting lectureres rate a TA<br />
	Issues encountered: Due to a lack of time in prepping for my computer graphics resit and other coursework, I didn't get the chance to work on this issue yet, so it will be moved to next week.<br />
	<br />
	Estimated time: 2-3 hours.<br />
	Actual time spent: -

Tasks Ravi:<br />
-	`issue 10`: Students should have passed the course before being able to apply for a position as a TA.<br /> 
The creation of the application was done using a chain of responsibility. And every class in the chain was responsible for checking certain requirements that often needed data from other microservices. This communication between microservices caused some problems.  We use a WebClient when communicating with other microservices but figuring those out took several iterations. Figuring out if they worked was quite hard because when trying to test it we needed to have at least two microservices up and running which at the time we couldn’t do for some reason. We later figured out that the server we used had a limited number of connections and that when multiple people are connecting to the servers those spots were being used up. To solve this we simply made it so only one person was testing their web clients at the same time.
