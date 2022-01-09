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

Tasks Iarina:<br />
- I did not have too many tasks since my collegues needed code contribution. I tried to plan next sprint for everybody and give people related issues with their previous work. I also tried to see if the application is working and I discovered that a lot of my collegues code was not doing the desired functionalities. I tried to help them fix that. I also fixed other people pipelines and spent a lot of time reviewing their code. I also started working on the final version of the first assignment , since I could’t code because I needed other people’s part.
- I found that the draft was not quite to the standard and I polished it a lot. I encountered the problem of realising the diagrams we have are not what is expected. I tried to think for that , but I did not finish it yet.
- For the next sprint, we saw that assigning tasks and not issues for one person is a bit confusing since you depend on someone’s work. We are trying from now to do issues entirely. We also need to improve communication since we had a lot of problems because code was not connected.

Tasks Pauline:<br />
- Issue 2: I had a task to make a webclient to request endpoints, I struggled at the start of the week to get the spring framework for this to work so I managed by making classes that format and send http requests manually, this took more time than it should have. I saw that by the end of the week other group memebers had gotten the spring version to work), so I ended redoing it in that method for consistency. 
- Issue 7: I had never used DTO's before and we planned on using them for this issue, so figuring out what they were and how to use them cost me alot of time.

Tasks Vincent:
- I did research on spring security and started to implement it