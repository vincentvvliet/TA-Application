## Week 6
Gijs:
- Issue 8:  Have endpoints and logic for letting lectureres rate a TA
	Issues encountered. Finished this week.
	
	Estimated time: 2-3 hours.\
	Actual time spent: 4 hours
	
- Issue 22: Have a TA Recommendation System.
    This issue was quite a lot bigger than I expected. I spent the rest of this week on this, and got about half way trough. At the start of this issue I did a lot of thinking on what a good way would be to do this system, and I think this really helped me understand what I had to do. I completly underestimated how much time this issue would take me, as I've not really made a lot of progress yet.

	Estimated time: 6-8 hours\
	Actual time spent: 6 hours. (of which about half was thinking and research)
	
---
Ravi:
- This week I found out that I had been misunderstanding some stuff about when exactly people could apply for a course. I always thought it was that a student could apply in the three week before the course start date. But it actually turned out to be that that is the end deadline. So I went back in the code to fix this issue by changing the if statements and tests that test for that thing.
- I ended up changing a lot of controllers and services to return the Mono object instead of an optional or a primitive, since it was decided that was the better way to implement the webclient.

---
Andrei:
- I have spent the entire week solving issue #4. It was one of the very first issues solved and because of this a lot of the foundation code had to be laid. There were no specific impediments, but it was a lengthy process
- I have also spent a considerable amount of time figuring out how to use web clients in the service classes
---

Tasks Pauline:<br />
- At the start of this week we decided to divide up most the remaining tasks to be able to plan ahead more. I ended up getting alot of 'could haves ' \(25,26,27\), meaning my code was dependant on others, which I brought up during this meeting but which we decided should not end up being a big issue. 
- I got message from Iarina on thursday that she needed help with her issues and assignment 1, and we decided to reallocate some of the work.I was going to help with issue 14 and she was going to take issue 25. I was going to leave issues 26,27 and take 28 and help with other issues on declaring hours. 
- By the time of the deadline there was a big issue concerning the deadline for assignent one, namely that the implementation for the proxy pattern we had planned was not going to work, as the pattern would make the use of spring security obsolete. I heard this from Iarina on friday noon; she and gijs were working on the implementations and needed help with the assignment itself and sumbitting it. I spent that day finishing writing the assignment, by rewriting certain parts for part 1, writing about the new design pattern for part 2, and submitting everything.

---
Vincent:
- I implemented the proxy design pattern for the user microservice.
- I added mockito tests for the secured user controller
- Unfortunately, after I spent a lot of time creating the Proxy design pattern, it was decided that we would remove this and create a new design pattern as this one was redundant. I did mention this from the start, however it was only realised later. Luckily, it was realised on time for the assignment deadline.
